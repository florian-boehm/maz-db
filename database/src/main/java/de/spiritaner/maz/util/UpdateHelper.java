package de.spiritaner.maz.util;

import de.spiritaner.maz.controller.LoginController;
import de.spiritaner.maz.model.User;
import de.spiritaner.maz.util.database.DataDatabase;
import de.spiritaner.maz.util.database.UserDatabase;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Florian Schwab
 * @version 2017.05.29
 */
public class UpdateHelper {

    private final static Logger logger = Logger.getLogger(LoginController.class);
    private final LoginController loginController;
    private final String releaseJsonString;
    private User user;
    private File sourceFile;
    private File backupFolder;
    private File binaryJarFile;

    public UpdateHelper(final LoginController loginController, final String releaseJsonString, final User user) {
        this.loginController = loginController;
        this.releaseJsonString = releaseJsonString;
        this.user = user;
    }

    public void startUpdate() {
        //loginController.getStage().setOnCloseRequest(event -> event.consume());

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                try {
                    //Platform.setImplicitExit(false);
                    loginController.setUpdateProgress("Vorbereitung des Updates!", ProgressIndicator.INDETERMINATE_PROGRESS);

                    downloadAssets();
                    downloadVersionZip();
                    extractLiquibaseChangelogs();
                    backupDatabaseFiles();
                    updateUserDatabase();
                    updateCoreDatabase();
                    createVersionFileInCurrentDir();
                    updateBatchFiles();
                    deleteVersionZip();
                    deleteLiquibaseFolder();

                    restartApplication();

                    loginController.setUpdateProgress("Update fertig, bitte Anwendung neustarten!", 1.0);
                } catch (Exception e) {
                    logger.error(e);
                    logger.error("Restoring backup");

                    final File targetDbDir = new File(Settings.get("database.path", "./dbfiles/"));

                    if (targetDbDir.exists() && backupFolder.exists()) {
                        loginController.setUpdateProgress("Backup wird wiederhergestellt!", ProgressIndicator.INDETERMINATE_PROGRESS);
                        FileUtils.deleteDirectory(targetDbDir);
                        FileUtils.copyDirectory(backupFolder, targetDbDir);
                    }

                    deleteVersionZip();
                    deleteLiquibaseFolder();
                } finally {
                    //Platform.setImplicitExit(true);
                }

                return null;
            }
        };

        new Thread(task).start();
    }

    private void deleteLiquibaseFolder() throws IOException {
        FileUtils.deleteDirectory(new File("./liquibase"));
    }

    private void updateUserDatabase() throws SQLException, ParseException, LiquibaseException {
        loginController.setUpdateProgress("Aktualisiere Benutzerdatenbank!", ProgressIndicator.INDETERMINATE_PROGRESS);
        final JSONObject releaseInfo = (JSONObject) new JSONParser().parse(releaseJsonString);
        final String version = (String) releaseInfo.get("tag_name");

        Map<String, String> properties = new HashMap<>();
        UserDatabase.initDatabaseProperties(properties, Settings.get("database.path", "./dbfiles/"));

        Connection conn = DriverManager.getConnection(properties.get("hibernate.connection.url"), "", "");
        JdbcConnection jdbcConn = new JdbcConnection(conn);
        Liquibase liquibase = new Liquibase("./liquibase/"+version+"/users/changelog.xml", new FileSystemResourceAccessor(), jdbcConn);
        liquibase.update("");

        logger.info("Database schema has been applied to user database!");
    }

    private void updateCoreDatabase() throws SQLException, ParseException, LiquibaseException {
        loginController.setUpdateProgress("Aktualisiere Stammdatenbank!", ProgressIndicator.INDETERMINATE_PROGRESS);
        final JSONObject releaseInfo = (JSONObject) new JSONParser().parse(releaseJsonString);
        final String version = (String) releaseInfo.get("tag_name");

        Map<String, String> properties = new HashMap<>();
        DataDatabase.initDatabaseProperties(properties, Settings.get("database.path", "./dbfiles/"), user);

        Connection conn = DriverManager.getConnection(properties.get("hibernate.connection.url"), user.getUsername(), DatatypeConverter.printHexBinary(user.getUnencryptedDatabaseKey()) + " " + user.getPassword());
        JdbcConnection jdbcConn = new JdbcConnection(conn);
        Liquibase liquibase = new Liquibase("./liquibase/"+version+"/data/changelog.xml", new FileSystemResourceAccessor(), jdbcConn);

        logger.info("Database schema has been applied to data database!");
    }

    private void restartApplication() throws URISyntaxException {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";

        // Build command: java -jar application.jar
        final ArrayList<String> command = new ArrayList<String>();
        command.add(javaBin);
        command.add("-jar");
        command.add(binaryJarFile.getPath());

        final ProcessBuilder builder = new ProcessBuilder(command);

        // Show auto close in masker
        new Thread(new Runnable() {

            private long timer = 3000;

            @Override
            public void run() {
                long before = System.currentTimeMillis();

                while(timer > 0) {
                    loginController.setUpdateProgress("Neustart in " + timer / 1000 + " Sekunden", (3000-timer)/1000.0);
                    timer -= (System.currentTimeMillis() - before);
                    before = System.currentTimeMillis();
                }

                try {
                    builder.start();
                    System.exit(0);
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }).start();
    }

    private void createVersionFileInCurrentDir() throws ParseException, IOException {
        final JSONObject releaseInfo = (JSONObject) new JSONParser().parse(releaseJsonString);
        final String version = (String) releaseInfo.get("tag_name");
        new File(Settings.get("database.path", "./dbfiles/") + version + ".version").createNewFile();
    }

    private void deleteVersionZip() {
        if (sourceFile != null && sourceFile.exists()) sourceFile.delete();
    }

    private void updateBatchFiles() throws IOException {
        final File runBatchOld = new File("./run_old.bat");
        final File runBatch = new File("./run.bat");

        if (runBatchOld.exists()) runBatchOld.delete();
        if (runBatch.exists()) {
            FileUtils.copyFile(runBatch, runBatchOld);
            runBatch.delete();
        }

        final PrintWriter writer = new PrintWriter(new FileWriter(runBatch));
        //start javaw -jar database-0.4-jar-with-dependencies.jar
        writer.write("start javaw -jar " + binaryJarFile.getPath() + "\n");
        writer.flush();
        writer.close();
    }

    private void backupDatabaseFiles() throws IOException {
        final File sourceDbDir = new File(Settings.get("database.path", "./dbfiles/"));

        if (sourceDbDir.exists()) {
            loginController.setUpdateProgress("Anlegen des Datenbank-Backups!", ProgressIndicator.INDETERMINATE_PROGRESS);

            final LocalDateTime now = LocalDateTime.now();
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
            backupFolder = new File(Settings.get("database.parent") + "/dbfiles-" + now.format(formatter) + "/");
            FileUtils.copyDirectory(sourceDbDir, backupFolder);
        }
    }

    private void extractLiquibaseChangelogs() throws IOException, SQLException, LiquibaseException, ParseException {
        if (sourceFile != null && sourceFile.exists()) {
            loginController.setUpdateProgress("Entpacke Liquibase-Dateien!", ProgressIndicator.INDETERMINATE_PROGRESS);

            final JSONObject releaseInfo = (JSONObject) new JSONParser().parse(releaseJsonString);
            final String version = (String) releaseInfo.get("tag_name");

            logger.info("Extract liquibase files from 'source.zip' to './liquibase'");

            final ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceFile.getAbsolutePath()));
            ZipEntry ze;
            byte[] buffer = new byte[1024];

            while ((ze = zis.getNextEntry()) != null) {
                String fileName = ze.getName();

                // Extract only files that are in the liquibase folder
                if (fileName.contains("liquibase")) {
                    fileName = fileName.substring(fileName.indexOf("liquibase"));
                    fileName = fileName.replace("liquibase", "liquibase/" + version);

                    if (ze.isDirectory()) {
                        new File("./" + fileName + "/").mkdirs();
                    } else {
                        File newFile = new File("./" + fileName);
                        new File(newFile.getParent()).mkdirs();

                        FileOutputStream fos = new FileOutputStream(newFile);

                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }

                        fos.close();
                        logger.info("File extract: " + newFile.getAbsoluteFile());
                    }
                }
            }

            zis.closeEntry();
            zis.close();

            //new File("./liquibase/"+version+".version").createNewFile();
        }
    }

    private void downloadVersionZip() throws IOException, ParseException {
        loginController.setUpdateProgress("Lade Update-Datei!", ProgressIndicator.INDETERMINATE_PROGRESS);

        final JSONObject releaseInfo = (JSONObject) new JSONParser().parse(releaseJsonString);
        final String zipUrl = (String) releaseInfo.get("zipball_url");
        sourceFile = new File("./source.zip");
        FileUtils.copyURLToFile(new URL(zipUrl), sourceFile);
        logger.info("Downloaded source to './source.zip'");
    }

    private void downloadAssets() throws IOException, ParseException {
        final JSONObject releaseInfo = (JSONObject) new JSONParser().parse(releaseJsonString);
        final JSONArray assetArray = (JSONArray) releaseInfo.get("assets");

        for (Object assetObj : assetArray) {
            final JSONObject asset = (JSONObject) assetObj;
            final String assetName = (String) asset.get("name");

            if (assetName.startsWith("database")) binaryJarFile = new File("./" + assetName);

            if (assetName.endsWith(".jar") && !new File(assetName).exists()) {
                final String downloadUrl = (String) asset.get("browser_download_url");
                final long fileSize = (Long) asset.get("size");

                loginController.setUpdateProgress("Lade Datei '" + assetName + "' herunter!", 0);

                final URL website = new URL(downloadUrl);
                final ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                final FileOutputStream fos = new FileOutputStream(assetName);

                long readBytes;
                long alreadyRead = 0L;
                int chunkSize = 4096;

                while ((readBytes = fos.getChannel().transferFrom(rbc, alreadyRead, chunkSize)) > 0) {
                    alreadyRead += readBytes;
                    final double percentage = (double) alreadyRead / (double) fileSize;

                    loginController.setUpdateProgress("Lade Datei '" + assetName + "' herunter!", percentage);
                }

                logger.info("Asset '" + assetName + "' downloaded!");
                logger.info("Filesize: " + alreadyRead + "/" + fileSize + " bytes");
            }
        }
    }
}

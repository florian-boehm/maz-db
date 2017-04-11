package de.spiritaner.maz.controller;

import de.spiritaner.maz.util.Settings;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import liquibase.exception.LiquibaseException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ProgressController implements Initializable {

	final static Logger logger = Logger.getLogger(ProgressController.class);

	@FXML
	private Text progressText;
	@FXML
	private Button closeButton;
	@FXML
	private ProgressBar progressBar;

	private Stage stage;
	private String releaseJsonString;
	private File sourceFile;
	private File backupFolder;
	private File binaryJarFile;
	private List<String> parameters;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void closeProgressDialog(ActionEvent actionEvent) {
		Platform.runLater(() -> stage.close());
	}

	public void setReleaseJsonString(String releaseJsonString) {
		this.releaseJsonString = releaseJsonString;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void startUpdate() {
		Task task = new Task() {
			@Override
			protected Object call() throws Exception {
				try {
					Platform.runLater(() -> {
						progressText.setText("Vorbereitung ...");
						progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
					});
					downloadAssets();
					downloadVersionZip();
					backupDatabaseFiles();
					extractLiquibaseChangelogs();
					createVersionFileInCurrentDir();
					updateBatchFiles();
					deleteVersionZip();

					Platform.runLater(() -> {
						progressText.setText("Update heruntergeladen! Bitte Anwendung komplett neustarten ...");
						progressBar.setProgress(1);
						closeButton.setDisable(false);
					});
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e);
				}

				return null;
			}
		};

		new Thread(task).start();
	}

	private void createVersionFileInCurrentDir() throws ParseException, IOException {
		JSONObject releaseInfo = (JSONObject) new JSONParser().parse(releaseJsonString);
		final String version = (String) releaseInfo.get("tag_name");
		new File(Settings.get("database.path","./dbfiles/") + version + ".version").createNewFile();
	}

	private void deleteVersionZip() {
		File sourceZip = new File("./source.zip");
		if(sourceZip.exists()) sourceZip.delete();
	}

	private void updateBatchFiles() throws IOException {
		File runBatchOld = new File("./run_old.bat");
		File runBatch = new File("./run.bat");

		if(runBatchOld.exists()) runBatchOld.delete();
		if(runBatch.exists()) {
			FileUtils.copyFile(runBatch, runBatchOld);
			runBatch.delete();
		}

		PrintWriter writer = new PrintWriter(new FileWriter(runBatch));
		//start javaw -jar database-0.4-jar-with-dependencies.jar
		writer.write("start javaw -jar "+binaryJarFile.getPath()+"\n");
		writer.flush();
		writer.close();
	}

	private void backupDatabaseFiles() throws IOException {
		if(new File("./dbfiles/").exists()) {
			Platform.runLater(() -> {
				progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
				progressText.setText("Anlegen des Datenbank-Backups ...");
			});

			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
			backupFolder = new File("./dbfiles-"+now.format(formatter)+"/");
			FileUtils.copyDirectory(new File("./dbfiles/"), backupFolder);
		}
	}

	private void extractLiquibaseChangelogs() throws IOException, SQLException, LiquibaseException, ParseException {
		if (sourceFile != null && sourceFile.exists()) {
			Platform.runLater(() -> {
				progressText.setText("Entpacke Liquibase-Dateien ...");
			});

			JSONObject releaseInfo = (JSONObject) new JSONParser().parse(releaseJsonString);
			final String version = (String) releaseInfo.get("tag_name");

			logger.info("Extract liquibase files from 'source.zip' to './liquibase'");

			ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceFile.getAbsolutePath()));
			ZipEntry ze;
			byte[] buffer = new byte[1024];

			while ((ze = zis.getNextEntry()) != null) {
				String fileName = ze.getName();

				// Extract only files that are in the liquibase folder
				if (fileName.contains("liquibase")) {
					fileName = fileName.substring(fileName.indexOf("liquibase"));

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

			new File("./liquibase/"+version+".version").createNewFile();
		}

		/**/
	}

	private void downloadVersionZip() throws IOException, ParseException {
		Platform.runLater(() -> {
			progressText.setText("Lade Update-Datei ...");
			progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
		});

		JSONObject releaseInfo = (JSONObject) new JSONParser().parse(releaseJsonString);
		String zipUrl = (String) releaseInfo.get("zipball_url");
		sourceFile = new File( "./source.zip");
		FileUtils.copyURLToFile(new URL(zipUrl), sourceFile);
		logger.info("Downloaded source to './source.zip'");
	}

	private void downloadAssets() throws IOException, ParseException {
		JSONObject releaseInfo = (JSONObject) new JSONParser().parse(releaseJsonString);
		JSONArray assetArray = (JSONArray) releaseInfo.get("assets");

		for (Object assetObj : assetArray) {
			JSONObject asset = (JSONObject) assetObj;
			String assetName = (String) asset.get("name");

			if(assetName.startsWith("database")) binaryJarFile = new File("./"+assetName);

			if (assetName.endsWith(".jar") && !new File(assetName).exists()) {
				String downloadUrl = (String) asset.get("browser_download_url");
				final long fileSize = (Long) asset.get("size");

				Platform.runLater(() -> {
					progressBar.setProgress(0);
					progressText.setText("Lade Datei '" + assetName + "' herunter ...");
				});

				URL website = new URL(downloadUrl);
				ReadableByteChannel rbc = Channels.newChannel(website.openStream());
				FileOutputStream fos = new FileOutputStream(assetName);

				long readBytes;
				long alreadyRead = 0L;
				int chunkSize = 4096;

				while ((readBytes = fos.getChannel().transferFrom(rbc, alreadyRead, chunkSize)) > 0) {
					alreadyRead += readBytes;
					final double percentage = (double) alreadyRead / (double) fileSize;

					Platform.runLater(() -> progressBar.setProgress(percentage));
				}

				logger.info("Asset '" + assetName + "' downloaded!");
				logger.info("Filesize: " + alreadyRead + "/" + fileSize + " bytes");
			}
		}
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	public List<String> getParameters() {
		return parameters;
	}
}

package de.spiritaner.maz.util.database;

import de.spiritaner.maz.model.User;
import de.spiritaner.maz.util.Settings;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.tool.schema.spi.SchemaManagementException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for accessing the database that stores productive data.
 *
 * @author Florian Schwab
 * @version 2017.05.28
 */
public class CoreDatabase {
    private static final Logger logger = Logger.getLogger(CoreDatabase.class);
    public static final String LOCK_FILE = "db.lock";
    private static final String DB_FILE_NAME = "core.mv.db";
    private static EntityManagerFactory factory = null;

    private CoreDatabase() {

    }

    synchronized static void initFactory(final User user) throws Exception {
        final String path = Settings.get("database.path", "./dbfiles/");
        final Map<String, String> properties = new HashMap<>();
        final File dataDbOrig = new File(path + DB_FILE_NAME);
        final File lockFile = new File(path + LOCK_FILE);
        final boolean exclusiveAccess = !lockFile.exists();

        try {
            if (!exclusiveAccess && factory == null) {
                try {
                    final File dataDbInTmp = File.createTempFile("maz-db-", "-" + DB_FILE_NAME, new File("./"));
                    dataDbInTmp.deleteOnExit();
                    FileUtils.copyFile(dataDbOrig, dataDbInTmp);

                    initDatabaseProperties(properties, dataDbInTmp.getPath().replace(DB_FILE_NAME, ""), user);
                    factory = Persistence.createEntityManagerFactory("dataDb", properties);

                    runAssetGeneration(properties.get("hibernate.connection.url"), user);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new Exception("Failed to create temporary file for inclusive access!");
                }
            } else if (exclusiveAccess && factory == null) {
                initDatabaseProperties(properties, path, user);
                factory = Persistence.createEntityManagerFactory("dataDb", properties);

                runAssetGeneration(properties.get("hibernate.connection.url"), user);
            }
        } catch (Exception e) {
            // Search for the cause of the exception
            Throwable t = e.getCause();

            boolean schemaManagementException = false;
            boolean illegalStateException = false;

            if (t != null) {
                do {
                    if (t instanceof SchemaManagementException) schemaManagementException = true;
                    if (t instanceof IllegalStateException) illegalStateException = true;
                } while ((t = t.getCause()) != null);
            }

            if (schemaManagementException) {
                throw new DatabaseException("Database schema is invalid!");
            } else if (illegalStateException) {
                throw new DatabaseException("Data database is already in use!");
            } else {
                throw e;
            }
        }

        if (exclusiveAccess && factory != null) {
            lockFile.createNewFile();
            lockFile.deleteOnExit();
        }
    }

    /**
     * (Re)create the assets in the core database
     *
     * @param url  The connection URL to the database
     * @param user The user that is needed for authentication and encryption
     */
    private static void runAssetGeneration(String url, User user) {
        try {
            logger.info("Try to create assets");
            Connection conn = DriverManager.getConnection(url, user.getUsername(), DatatypeConverter.printHexBinary(user.getUnencryptedDatabaseKey()) + " " + user.getPassword());
            JdbcConnection jdbcConn = new JdbcConnection(conn);
            Liquibase liquibase = new Liquibase("./liquibase/assets/changelog.xml", new ClassLoaderResourceAccessor(), jdbcConn);
            liquibase.update("");
            logger.warn("Assets have been successfully created!");
        } catch (LiquibaseException | SQLException e) {
            logger.error(e);
        }
    }

    /**
     * This method tries to run the liquibase update on the specified connection URL
     *
     * @param url  The connection URL to the database
     * @param user The user that is needed for authentication and encryption
     * @throws SQLException       Thrown in case of a problem with the underlying sql statements
     * @throws LiquibaseException Thrown in case of a problem with liquibase itself
     */
    private static void runLiquibaseUpdate(String url, User user) throws SQLException, LiquibaseException {
        Connection conn = DriverManager.getConnection(url, user.getUsername(), DatatypeConverter.printHexBinary(user.getUnencryptedDatabaseKey()) + " " + user.getPassword());
        JdbcConnection jdbcConn = new JdbcConnection(conn);
        Liquibase liquibase = new Liquibase("liquibase/core/changelog.xml", new ClassLoaderResourceAccessor(), jdbcConn);
        liquibase.update("");

        logger.info("Database schema has been applied to core database!");
    }

    /**
     * This method returns the EntityManagerFactory
     *
     * @return The EntityManagerFactory, may be null if it was not initialized correctly
     */
    public static EntityManagerFactory getFactory() {
        return factory;
    }

    /**
     * A wrapper method for setting the database parameters
     *
     * @param properties The properties map, will be cleared before initialization!
     * @param path       The path to the database file
     * @param user       The user that is needed for authentication and encryption
     */
    private static void initDatabaseProperties(Map<String, String> properties, String path, User user) {
        properties.clear();

        String url = "jdbc:h2:" + path + "core;CIPHER=AES";
        url += ";LOCK_TIMEOUT=" + Settings.get("database.core.lock_timeout", "5");
        url += ";DEFAULT_LOCK_TIMEOUT=" + Settings.get("database.core.default_lock_timeout", "5");
        url += ";TRACE_LEVEL_FILE=" + Settings.get("database.core.trace_level_file", "0");
        url += ";TRACE_LEVEL_SYSTEM_OUT=" + Settings.get("database.core.trace_level_system_out", "1");

        properties.put("hibernate.connection.url", url);
        properties.put("hibernate.connection.username", user.getUsername());
        properties.put("hibernate.connection.password", DatatypeConverter.printHexBinary(user.getUnencryptedDatabaseKey()) + " " + user.getPassword());
        properties.put("javax.persistence.jdbc.username", user.getUsername());
        properties.put("javax.persistence.jdbc.password", DatatypeConverter.printHexBinary(user.getUnencryptedDatabaseKey()) + " " + user.getPassword());
    }

    public static void init(User user) throws SQLException, LiquibaseException {
        Map<String, String> properties = new HashMap<>();
        initDatabaseProperties(properties, Settings.get("database.path", "./dbfiles/"), user);
        runLiquibaseUpdate(properties.get("hibernate.connection.url"), user);
    }
}

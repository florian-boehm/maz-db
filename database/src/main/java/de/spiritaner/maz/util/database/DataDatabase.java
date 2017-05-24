package de.spiritaner.maz.util.database;

import de.spiritaner.maz.model.User;
import de.spiritaner.maz.util.Settings;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class DataDatabase {
	private static final Logger logger = Logger.getLogger(DataDatabase.class);
	private static EntityManagerFactory factory = null;
	private static final String LOCK_FILE = "db.lock";
	private static final String DB_FILE_NAME = "data.mv.db";

	private DataDatabase() {

	}

	synchronized static void initFactory(final User user) throws Exception {
		final String path = Settings.get("database.path", "./dbfiles/");
		final Map<String, String> properties = new HashMap<>();
		final File dataDbOrig = new File(path + DB_FILE_NAME);
		final File lockFile = new File(path+LOCK_FILE);
		final boolean exclusiveAccess = !lockFile.exists();

		if (!dataDbOrig.exists()) {
			// If the original database does not exist we just create a new one
			initDatabaseProperties(properties, path, user);
			runLiquibaseUpdate(properties.get("hibernate.connection.url"), user);
		}

		if(!exclusiveAccess) {
			boolean result = askForInclusiveAccess();

			if(!result) {
				logger.error("Database is in use but inclusive mode was denied by the user! Exit now ...");
				Platform.exit();
			}
		}

		try {
			if(!exclusiveAccess && factory == null) {
				try {
					final File dataDbInTmp = File.createTempFile("maz-db-", "-" + DB_FILE_NAME, new File("./"));
					dataDbInTmp.deleteOnExit();
					FileUtils.copyFile(dataDbOrig, dataDbInTmp);

					initDatabaseProperties(properties, dataDbInTmp.getPath().replace(DB_FILE_NAME, ""), user);
					factory = Persistence.createEntityManagerFactory("dataDb", properties);

					runAssetGeneration(properties.get("hibernate.connection.url"), user);
				} catch (IOException e) {
					throw new Exception("Failed to create temporary file for inclusive access!");
				}
			} else if(exclusiveAccess && factory == null) {
				initDatabaseProperties(properties, path, user);
				factory = Persistence.createEntityManagerFactory("dataDb", properties);

				runAssetGeneration(properties.get("hibernate.connection.url"), user);
			}
		} catch (Exception e) {
			// Search for the cause of the exception
			Throwable t = e.getCause();

			boolean schemaManagementException = false;
			boolean illegalStateException = false;

			if(t != null) {
				do {
					if (t instanceof SchemaManagementException) schemaManagementException = true;
					if (t instanceof IllegalStateException) illegalStateException = true;
				} while ((t = t.getCause()) != null);
			}

			if(schemaManagementException) {
				final String version = ResourceBundle.getBundle("lang.gui").getString("version");
				final File updateFile = new File(path+""+version+".data.update");

				logger.warn("Schema of the data database is not valid and update file " + ((updateFile.exists()) ? "exists!" : "does not exist!"));

				if(updateFile.exists()) {
					runLiquibaseUpdate(properties.get("hibernate.connection.url"), user);

					if(!exclusiveAccess && factory == null) {
						factory = Persistence.createEntityManagerFactory("dataDb", properties);
						updateFile.delete();
					} else if(exclusiveAccess && factory == null) {
						factory = Persistence.createEntityManagerFactory("dataDb", properties);
						updateFile.delete();
					}
				} else {
					throw new DatabaseException("Database schema is invalid but no update file was found!");
				}
			} else if(illegalStateException) {
				throw new DatabaseException("Data database is already in use!");
			} else {
				throw e;
			}
		}

		if(exclusiveAccess && factory != null) {
			lockFile.createNewFile();
			lockFile.deleteOnExit();
		}
	}

	/**
	 * (Re)create the assets in the data database
	 *
	 * @param url The connection URL to the database
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
	 * @param url The connection URL to the database
	 * @param user The user that is needed for authentication and encryption
	 * @throws SQLException Thrown in case of a problem with the underlying sql statements
	 * @throws LiquibaseException Thrown in case of a problem with liquibase itself
	 */
	private static void runLiquibaseUpdate(String url, User user) throws SQLException, LiquibaseException {
		Connection conn = DriverManager.getConnection(url, user.getUsername(), DatatypeConverter.printHexBinary(user.getUnencryptedDatabaseKey()) + " " + user.getPassword());
		JdbcConnection jdbcConn = new JdbcConnection(conn);
		Liquibase liquibase = new Liquibase("./liquibase/data/changelog.xml", new ClassLoaderResourceAccessor(), jdbcConn);
		liquibase.update("");

		logger.info("Database schema has been applied to data database!");
		logger.info("Try to reopen data database now!");
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
	 * Ask the user if he accepts the inclusive access to the database
	 *
	 * @return True, if the user accepts the inclusive access. Otherwise: false
	 */
	private static boolean askForInclusiveAccess() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Lesezugriff");
		alert.setHeaderText("Die Datenbank ist bereits in Verwendung ...");
		alert.setContentText("Möchten Sie eine Kopie der Datenbank öffnen? (Alle Änderungen, die Sie machen gehen beim Schließen der Anwendung verloren)");

		Optional<ButtonType> result = alert.showAndWait();
		return result.isPresent() && result.get() == ButtonType.OK;
	}

	/**
	 * A wrapper method for setting the database parameters
	 *  @param properties The properties map, will be cleared before initialization!
	 * @param path The path to the database file
	 * @param user The user that is needed for authentication and encryption
	 */
	private static void initDatabaseProperties(Map<String, String> properties, String path, User user) {
		properties.clear();

		String url = "jdbc:h2:"+path+"data;CIPHER=AES";
		url += ";LOCK_TIMEOUT=" + Settings.get("database.data.lock_timeout","5");
		url += ";DEFAULT_LOCK_TIMEOUT=" + Settings.get("database.data.default_lock_timeout","5");
		url += ";TRACE_LEVEL_FILE=" + Settings.get("database.data.trace_level_file","0");
		url += ";TRACE_LEVEL_SYSTEM_OUT=" + Settings.get("database.data.trace_level_system_out","1");

		properties.put("hibernate.connection.url", url);
		properties.put("hibernate.connection.username", user.getUsername());
		properties.put("hibernate.connection.password", DatatypeConverter.printHexBinary(user.getUnencryptedDatabaseKey()) + " " + user.getPassword());
		properties.put("javax.persistence.jdbc.username", user.getUsername());
		properties.put("javax.persistence.jdbc.password", DatatypeConverter.printHexBinary(user.getUnencryptedDatabaseKey()) + " " + user.getPassword());
	}
}

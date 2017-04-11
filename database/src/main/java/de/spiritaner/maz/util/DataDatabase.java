package de.spiritaner.maz.util;

import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.model.User;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import org.apache.log4j.Logger;
import org.hibernate.tool.schema.spi.SchemaManagementException;

import javax.persistence.*;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class DataDatabase {
	private static final Logger logger = Logger.getLogger(DataDatabase.class);
	private static EntityManagerFactory factory = null;


	private DataDatabase() {

	}

	public synchronized static void initFactory(User user) {
		String path = Settings.get("database.path", "./dbfiles/");
		String url = "jdbc:h2:"+path+"data;CIPHER=AES";
		url += ";LOCK_TIMEOUT=" + Settings.get("database.data.lock_timeout","5");
		url += ";DEFAULT_LOCK_TIMEOUT=" + Settings.get("database.data.default_lock_timeout","5");
		url += ";TRACE_LEVEL_FILE=" + Settings.get("database.data.trace_level_file","0");
		url += ";TRACE_LEVEL_SYSTEM_OUT=" + Settings.get("database.data.trace_level_system_out","1");

		Map properties = new HashMap<>();
		properties.put("hibernate.connection.url", url);
		properties.put("hibernate.connection.username", user.getUsername());
		properties.put("hibernate.connection.password", DatatypeConverter.printHexBinary(user.getUnencryptedDatabaseKey()) + " " + user.getPassword());
		properties.put("javax.persistence.jdbc.username", user.getUsername());
		properties.put("javax.persistence.jdbc.password", DatatypeConverter.printHexBinary(user.getUnencryptedDatabaseKey()) + " " + user.getPassword());

		try {
			if (factory == null) {
				factory = Persistence.createEntityManagerFactory("dataDb", properties);
			}
		} catch (Exception e) {// Search for the cause of the exception
			final Throwable rootCause = e.getCause();
			Throwable t = rootCause;

			boolean schemaManagementException = false;
			boolean illegalStateException = false;

			do {
				if(t instanceof SchemaManagementException) schemaManagementException = true;
				if(t instanceof IllegalStateException) illegalStateException = true;
			} while((t = t.getCause()) != null);

			if(schemaManagementException) {
				logger.warn("Schema of the data database is not valid! Searching for liquibase files ...");
				File liquibaseDir = new File("./liquibase");
				File liquibaseVersion = new File("./liquibase/"+ ResourceBundle.getBundle("lang.gui").getString("version")+".version");

				if(liquibaseDir.exists() && liquibaseDir.isDirectory() && liquibaseVersion.exists()) {
					logger.info("Found liquibase directory that matches the current version!");
					logger.warn("Applying data database schema with liquibase now!");
					File dataChangelog = new File("./liquibase/data/changelog.xml");

					try {
						Connection conn = DriverManager.getConnection(url, user.getUsername(), DatatypeConverter.printHexBinary(user.getUnencryptedDatabaseKey()) + " " + user.getPassword());
						JdbcConnection jdbcConn = new JdbcConnection(conn);
						Liquibase liquibase = new Liquibase("./liquibase/data/changelog.xml", new FileSystemResourceAccessor(), jdbcConn);
						liquibase.update("");
						logger.warn("Database schema has been applied to data database!");

						if (factory == null) {
							factory = Persistence.createEntityManagerFactory("dataDb", properties);
						}
					} catch (LiquibaseException | SQLException e1) {
						logger.error(e1);
					}
				}
			} else if(illegalStateException) {
				logger.warn("Data database is already in use!");
				// TODO open read only here if necessary
			} else {
				logger.error(e);
				ExceptionDialog.show(e);
			}
		}
	}

	public static void initDataDatabase() {
		if (factory == null)
			return;

		EntityManager em = factory.createEntityManager();
	}

	public static EntityManagerFactory getFactory() {
		return factory;
	}
}

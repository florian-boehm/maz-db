package de.spiritaner.maz.util.database;

import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.model.User;
import de.spiritaner.maz.util.Settings;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.KeyGenerator;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * This class is a service class for accessing the user database through an entity manager
 *
 * @author Florian Schwab
 * @version 2017.05.23
 */
public class UserDatabase {

	private static final Logger logger = Logger.getLogger(UserDatabase.class);
	private static EntityManagerFactory exclusiveFactory = null;
	private static EntityManagerFactory inclusiveFactory = null;
	private static final String DB_FILE_NAME = "users.mv.db";

	/**
	 * This method creates the EntityManagerFactory if it was not created before
	 *
	 * @param exclusiveAccess If you need exclusive write access to the original user database then set this to 'true'
	 * @return If the creation was successfull you
	 */
	private synchronized static EntityManagerFactory getFactory(final boolean exclusiveAccess) throws Exception {
		// Retrieve the database folder from settings or the user specific path
		final String path = Settings.get("database.path", "./dbfiles/");
		final Map<String, String> properties = new HashMap<>();
		final File userDbOrig = new File(path + DB_FILE_NAME);

		if(!userDbOrig.exists()) {
			logger.info("User database needs to be initialized once!");
			// If the original database does not exist we just create a new one
			initDatabaseProperties(properties, path);
			runLiquibaseUpdate(properties.get("hibernate.connection.url"));
		}

		try {
			// If no exclusive access is needed, then we can copy the database to the systems preferred temporary directory
			if(!exclusiveAccess) {
				if(inclusiveFactory == null) {
					try {
						final File userDbInTmp = File.createTempFile("maz-db-", "-"+DB_FILE_NAME, new File("./"));
						userDbInTmp.deleteOnExit();
						FileUtils.copyFile(userDbOrig, userDbInTmp);

						initDatabaseProperties(properties, userDbInTmp.getPath().replace(DB_FILE_NAME,""));
						inclusiveFactory = Persistence.createEntityManagerFactory("userDb", properties);
					} catch (IOException e) {
						e.printStackTrace();
						throw new Exception("Failed to create temporary file for inclusive access!");
					}
				}

				return inclusiveFactory;
			} else {
				// Otherwise check if the factory was already
				if(exclusiveFactory == null) {
					initDatabaseProperties(properties, path);
					exclusiveFactory = Persistence.createEntityManagerFactory("userDb", properties);
				}

				return exclusiveFactory;
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
				final File updateFile = new File(path+""+version+".users.update");

				logger.warn("Schema of the user database is not valid and update file " + ((updateFile.exists()) ? "exists!" : "does not exist!"));

				if(updateFile.exists()) {
					runLiquibaseUpdate(properties.get("hibernate.connection.url"));

					if(!exclusiveAccess && inclusiveFactory == null) {
						inclusiveFactory = Persistence.createEntityManagerFactory("userDb", properties);
						updateFile.delete();
						return inclusiveFactory;
					} else if(exclusiveAccess && exclusiveFactory == null) {
						exclusiveFactory = Persistence.createEntityManagerFactory("userDb", properties);
						updateFile.delete();
						return exclusiveFactory;
					}
				} else {
					throw new DatabaseException("Database schema is invalid but no update file was found!");
				}
			} else if (illegalStateException) {
				throw new DatabaseException("User database is already in use!");
			} else {
				throw e;
			}
		}

		throw new DatabaseException("User database could not have been created");
	}

	/**
	 * This method tries to run the liquibase update on the specified connection URL
	 *
	 * @param url The connection URL to the database
	 * @throws SQLException Thrown in case of a problem with the underlying sql statements
	 * @throws LiquibaseException Thrown in case of a problem with liquibase itself
	 */
	private static void runLiquibaseUpdate(String url) throws SQLException, LiquibaseException {
		Connection conn = DriverManager.getConnection(url, "", "");
		JdbcConnection jdbcConn = new JdbcConnection(conn);
		Liquibase liquibase = new Liquibase("./liquibase/users/changelog.xml", new ClassLoaderResourceAccessor(), jdbcConn);
		liquibase.update("");

		logger.info("Database schema has been applied to user database!");
		logger.info("Try to reopen user database now!");
	}

	/**
	 * Checks if the user database contains at least one user
	 *
	 * @return	boolean	true if at least one user exists; otherwise: false
	 */
	public static boolean isPopulated() throws Exception {
		EntityManagerFactory em = getFactory(false);
		return (em.createEntityManager().createNamedQuery("User.findAll").getResultList().size() != 0);
	}

	/**
	 * Populate the database with the first user as important step of the database initialization
	 *
	 * @param username The name of the first user
	 * @param password The password of the first user
	 */
	public static void createFirstUser(String username, String password) {
		EntityManager em = null;

		try {
			em = getFactory(true).createEntityManager();
			em.getTransaction().begin();

			// Create the first user object and set it up.
			User adminUser = new User();
			adminUser.setUsername(username);
			adminUser.setPassword(password);

			// Use the key generator to get the database key.
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);

			// Add the database key to the user object. The user object will take care of its encryption on persist.
			adminUser.setUnencryptedDatabaseKey(keyGenerator.generateKey().getEncoded());

			// Persist the first user.
			em.persist(adminUser);
			em.getTransaction().commit();

			// If the admin user is persist then we initialize the data db
			adminUser.setPassword(password);
			DataDatabase.initFactory(adminUser);
		} catch (DatabaseException e) {
			// The entity manager could not be created
			logger.error(e);
			ExceptionDialog.show(e);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
			ExceptionDialog.show(e);

			// If something goes wrong, rollback the transaction.
			if(em != null) em.getTransaction().rollback();
		} catch (Exception e) {
			logger.error(e);
			ExceptionDialog.show(e);
		}
	}

	/**
	 * This method is used to valid a username and password combination against the database
	 *
	 * @param username The name of the user that needs to be validated
	 * @param password The password of the user
	 * @return
	 */
	public static boolean validateLogin(String username, String password) {
		try {
			EntityManager em = getFactory(false).createEntityManager();
			User user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();

			boolean passwordCorrect = BCrypt.checkpw(password, user.getPasswordHash());

			if (passwordCorrect) {
				user.setPassword(password);
				DataDatabase.initFactory(user);
				// TODO disable this here before release!
				logger.info("Decrypted database aes key is '" + DatatypeConverter.printHexBinary(user.getUnencryptedDatabaseKey()) + "'");
			}

			return passwordCorrect;
		} catch (Exception e) {
			logger.error(e);
			ExceptionDialog.show(e);
		}

		return false;
	}

	/**
	 * A wrapper method for setting the database parameters
	 *
	 * @param properties The properties map, will be cleared before initialization!
	 * @param path The path to the database file
	 */
	private static void initDatabaseProperties(Map<String, String> properties, String path) {
		properties.clear();

		String url = "jdbc:h2:"+path+"users";
		url += ";LOCK_TIMEOUT=" + Settings.get("database.user.lock_timeout","5");
		url += ";DEFAULT_LOCK_TIMEOUT=" + Settings.get("database.user.default_lock_timeout","5");
		url += ";TRACE_LEVEL_FILE=" + Settings.get("database.user.trace_level_file","0");
		url += ";TRACE_LEVEL_SYSTEM_OUT=" + Settings.get("database.user.trace_level_system_out","1");

		properties.put("hibernate.connection.url", url);
	}
}

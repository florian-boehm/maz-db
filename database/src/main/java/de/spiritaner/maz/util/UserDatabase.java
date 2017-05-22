package de.spiritaner.maz.util;

import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.model.User;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import org.apache.log4j.Logger;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.KeyGenerator;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
public class UserDatabase {

	private static final Logger logger = Logger.getLogger(UserDatabase.class);
	private static EntityManagerFactory factory = null;

	private synchronized static EntityManagerFactory getFactory() {
		Map properties = new HashMap<>();

		// TODO copy paste

		String path = Settings.get("database.path", "./dbfiles/");
		String url = "jdbc:h2:"+path+"users";
		url += ";LOCK_TIMEOUT=" + Settings.get("database.user.lock_timeout","5");
		url += ";DEFAULT_LOCK_TIMEOUT=" + Settings.get("database.user.default_lock_timeout","5");
		url += ";TRACE_LEVEL_FILE=" + Settings.get("database.user.trace_level_file","0");
		url += ";TRACE_LEVEL_SYSTEM_OUT=" + Settings.get("database.user.trace_level_system_out","1");

		properties.put("hibernate.connection.url", url);

		try {
			if(factory == null) {
				factory = Persistence.createEntityManagerFactory("userDb", properties);
			}
		} catch (Exception e) {
			// Search for the cause of the exception
			final Throwable rootCause = e.getCause();
			Throwable t = rootCause;

			boolean schemaManagementException = false;
			boolean illegalStateException = false;

			do {
				if(t instanceof SchemaManagementException) schemaManagementException = true;
				if(t instanceof IllegalStateException) illegalStateException = true;
			} while((t = t.getCause()) != null);

			if(schemaManagementException) {
				final String version = ResourceBundle.getBundle("lang.gui").getString("version");

				logger.warn("Schema of the user database is not valid! Searching for liquibase files ...");
				File liquibaseDir = new File("./liquibase");
				File liquibaseVersionDir = new File("./liquibase/"+version+"/");

				if(liquibaseDir.exists() && liquibaseDir.isDirectory() && liquibaseVersionDir.exists() && liquibaseVersionDir.isDirectory()) {
					logger.info("Found liquibase directory that matches the current version!");
					logger.warn("Applying user database schema with liquibase now!");
					File userChangelog = new File(liquibaseVersionDir.getPath()+"/users/changelog.xml");

					try {
						Connection conn = DriverManager.getConnection(url, "", "");
						JdbcConnection jdbcConn = new JdbcConnection(conn);
						Liquibase liquibase = new Liquibase(userChangelog.getAbsolutePath(), new FileSystemResourceAccessor(), jdbcConn);
						liquibase.update("");
						logger.info("Database schema has been applied to user database!");
						logger.info("Try to reopen user database now!");

						if(factory == null) {
							factory = Persistence.createEntityManagerFactory("userDb", properties);
						}
					} catch (LiquibaseException | SQLException e1) {
						logger.error(e1);
					}
				}
			} else if (illegalStateException) {
				logger.warn("User database is already in use!");
				// TODO open read only here if necessary
			} else {
				logger.error(e);
				ExceptionDialog.show(e);
			}
		}

		return factory;
	}

	/**
	 * Checks if the user database contains at least one user
	 *
	 * @return	boolean	true if at least one user exists; otherwise: false
	 */
	public static boolean isPopulated() throws Exception {
		EntityManagerFactory em = getFactory();
		if(em == null) throw new Exception("Database connection could not be opened!");

		return (em.createEntityManager().createNamedQuery("User.findAll").getResultList().size() != 0);
	}

	public static void createFirstUser(String username, String password) {
		EntityManager em = getFactory().createEntityManager();
		em.getTransaction().begin();

		try {
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
			DataDatabase.initDataDatabase();
		} catch (NoSuchAlgorithmException e) {
			ExceptionDialog.show(e);

			// If something goes wrong, rollback the transaction.
			em.getTransaction().rollback();
		}
	}

	public static boolean testLogin(String username, String password) {
		EntityManager em = getFactory().createEntityManager();
		Collection<User> result = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getResultList();
		Iterator<User> iterator = result.iterator();

		while(iterator.hasNext()) {
			User tmpUser = iterator.next();
			boolean passwordCorrect = BCrypt.checkpw(password, tmpUser.getPasswordHash());

			if(passwordCorrect) {
				tmpUser.setPassword(password);
				DataDatabase.initFactory(tmpUser);
				// TODO disable this here before release!
				logger.info("Decrypted database aes key is '"+ DatatypeConverter.printHexBinary(tmpUser.getUnencryptedDatabaseKey())+"'");
			}

			return passwordCorrect;
		}

		return false;
	}
}

package de.spiritaner.maz.util;

import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.model.User;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.log4j.Logger;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.KeyGenerator;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
public class UserDatabase {

	private static final Logger logger = Logger.getLogger(UserDatabase.class);
	private static EntityManagerFactory factory = null;

	private synchronized static EntityManagerFactory getFactory() {
		try {
			if(factory == null) {
				factory = Persistence.createEntityManagerFactory("userDb");
			}
		} catch (Exception e) {
			// TODO This should only be done in extra updater application
			Throwable t = e.getCause();

			while ((t != null) && !(t instanceof SchemaManagementException)) {
				t = t.getCause();
			}

			if(t instanceof SchemaManagementException) {
				logger.warn("A new database schema has to be applied!");

				try {
					Connection conn = DriverManager.getConnection("jdbc:h2:./dbfiles/users", "", "");
					JdbcConnection jdbcConn = new JdbcConnection(conn);
					Liquibase liquibase = new Liquibase("./liquibase/users/changelog.xml", new ClassLoaderResourceAccessor(), jdbcConn);
					liquibase.update("");
					logger.warn("Database schema has been applied!");

					factory = Persistence.createEntityManagerFactory("userDb");
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (LiquibaseException e1) {
					e1.printStackTrace();
				}
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
	public static boolean isPopulated() {
		return (getFactory().createEntityManager().createNamedQuery("User.findAll").getResultList().size() != 0);
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

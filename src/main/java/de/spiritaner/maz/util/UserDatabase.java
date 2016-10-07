package de.spiritaner.maz.util;

import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.model.User;
import org.apache.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.KeyGenerator;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.DatatypeConverter;
import javax.xml.crypto.Data;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
public class UserDatabase {

	private static final Logger logger = Logger.getLogger(UserDatabase.class);
	private static final EntityManagerFactory factory = Persistence.createEntityManagerFactory("userDb");

	/**
	 * Checks if the user database contains at least one user
	 *
	 * @return	boolean	true if at least one user exists; otherwise: false
	 */
	public static boolean isPopulated() {
		return (factory.createEntityManager().createNamedQuery("User.findAll").getResultList().size() != 0);
	}

	public static void createFirstUser(String username, String password) {
		EntityManager em = factory.createEntityManager();
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
		EntityManager em = factory.createEntityManager();
		Collection<User> result = em.createNamedQuery("User.findByUsername").setParameter("username", username).getResultList();
		Iterator<User> iterator = result.iterator();

		while(iterator.hasNext()) {
			User tmpUser = iterator.next();
			boolean passwordCorrect = BCrypt.checkpw(password, tmpUser.getPasswordHash());

			if(passwordCorrect) {
				tmpUser.setPassword(password);
				DataDatabase.initFactory(tmpUser);
//				System.out.println("Decrypted database aes key is '"+ DatatypeConverter.printHexBinary(tmpUser.getUnencryptedDatabaseKey())+"'");
			}

			return passwordCorrect;
		}

		return false;
	}
}

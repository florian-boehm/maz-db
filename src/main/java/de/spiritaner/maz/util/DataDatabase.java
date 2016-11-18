package de.spiritaner.maz.util;

import de.spiritaner.maz.model.Gender;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Salutation;
import de.spiritaner.maz.model.User;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.DatatypeConverter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Florian on 9/26/2016.
 */
public class DataDatabase {
	private static final Logger logger = Logger.getLogger(DataDatabase.class);
	private static EntityManagerFactory factory = null;

	public static void initFactory(User user) {
		Map properties = new HashMap<>();
		properties.put("hibernate.connection.username", user.getUsername());
		properties.put("hibernate.connection.password", DatatypeConverter.printHexBinary(user.getUnencryptedDatabaseKey())+" "+user.getPassword());
		properties.put("javax.persistence.jdbc.username", user.getUsername());
		properties.put("javax.persistence.jdbc.password", DatatypeConverter.printHexBinary(user.getUnencryptedDatabaseKey())+" "+user.getPassword());

		factory = Persistence.createEntityManagerFactory("dataDb", properties);
	}

	public static void initDataDatabase() {
		if(factory == null)
			return;

		EntityManager em = factory.createEntityManager();

		// Create default gender
		Gender male = new Gender();
		male.setDescription("Männlich");

		Gender female = new Gender();
		female.setDescription("Weiblich");

		// Create default salutations
		Salutation du = new Salutation();
		du.setDescription("Du");

		Salutation sie = new Salutation();
		sie.setDescription("Sie");

		// Create default person
		Person person = new Person();
		person.setGender(female);
		person.setFirstName("Momo");
		person.setBirthName("Musterperson");
		person.setBirthplace("Musterstadt");
		person.setSalutation(du);
		person.setBirthday(LocalDate.now());

		em.getTransaction().begin();
		em.persist(du);
		em.persist(sie);
		em.persist(person);
		em.persist(male);
		em.getTransaction().commit();
	}

	public static EntityManagerFactory getFactory() {
		return factory;
	}

	private DataDatabase() {

	}
}

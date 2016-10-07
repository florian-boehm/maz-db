package de.spiritaner.maz.util;

import de.spiritaner.maz.model.Gender;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.User;
import org.apache.log4j.Logger;
import org.apache.log4j.or.ObjectRenderer;
import org.hibernate.SessionFactory;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.DatatypeConverter;
import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.util.Date;
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

		// Create default person
		Person person = new Person();
		person.setGender(female);
		person.setPrename("Momo");
		person.setSurname("Musterperson");
		person.setBirthplace("Musterstadt");
		person.setBirthday(LocalDate.now());

		em.getTransaction().begin();
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

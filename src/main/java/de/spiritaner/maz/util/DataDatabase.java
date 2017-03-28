package de.spiritaner.maz.util;

import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.model.User;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.log4j.Logger;
import org.hibernate.tool.schema.spi.SchemaManagementException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.DatatypeConverter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DataDatabase {
	private static final Logger logger = Logger.getLogger(DataDatabase.class);
	private static EntityManagerFactory factory = null;

	public synchronized static void initFactory(User user) {
		Map properties = new HashMap<>();
		properties.put("hibernate.connection.username", user.getUsername());
		properties.put("hibernate.connection.password", DatatypeConverter.printHexBinary(user.getUnencryptedDatabaseKey())+" "+user.getPassword());
		properties.put("javax.persistence.jdbc.username", user.getUsername());
		properties.put("javax.persistence.jdbc.password", DatatypeConverter.printHexBinary(user.getUnencryptedDatabaseKey())+" "+user.getPassword());

		try {
			if(factory == null) {
				factory = Persistence.createEntityManagerFactory("dataDb", properties);
			}
		} catch (Exception e) {
			// TODO this is not good here!
			Throwable t = e.getCause();

			while ((t != null) && !(t instanceof SchemaManagementException)) {
				t = t.getCause();
			}

			if(t instanceof SchemaManagementException) {
				logger.warn("A new database schema has to be applied!");

				try {
					Connection conn = DriverManager.getConnection("jdbc:h2:./dbfiles/data;CIPHER=AES", user.getUsername(), DatatypeConverter.printHexBinary(user.getUnencryptedDatabaseKey())+" "+user.getPassword());
					JdbcConnection jdbcConn = new JdbcConnection(conn);
					Liquibase liquibase = new Liquibase("./liquibase/data/changelog.xml", new ClassLoaderResourceAccessor(), jdbcConn);
					liquibase.update("");
					logger.warn("Database schema has been applied!");

					factory = Persistence.createEntityManagerFactory("dataDb", properties);
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (LiquibaseException e1) {
					e1.printStackTrace();
				}
			} else {
				ExceptionDialog.show(e);
			}
		}
	}

	public static void initDataDatabase() {
		if(factory == null)
			return;

		EntityManager em = factory.createEntityManager();

		// Create default gender
		/*Gender male = new Gender();
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
		person.setFamilyName("Musterperson");
		person.setBirthplace("Musterstadt");
		person.setSalutation(du);
		person.setBirthday(LocalDate.now());

		em.getTransaction().begin();
		em.persist(du);
		em.persist(sie);
		em.persist(person);
		em.persist(male);
		em.getTransaction().commit();*/
	}

	public static EntityManagerFactory getFactory() {
		return factory;
	}

	private DataDatabase() {

	}
}

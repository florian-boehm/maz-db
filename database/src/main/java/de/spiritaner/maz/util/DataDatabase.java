package de.spiritaner.maz.util;

import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.model.User;
import org.apache.log4j.Logger;
import org.hibernate.tool.schema.spi.SchemaManagementException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.DatatypeConverter;
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
				// TODO start updater
			} else {
				ExceptionDialog.show(e);
			}
		}
	}

	public static void initDataDatabase() {
		if(factory == null)
			return;

		EntityManager em = factory.createEntityManager();
	}

	public static EntityManagerFactory getFactory() {
		return factory;
	}

	private DataDatabase() {

	}
}

package de.spiritaner.maz;

import javafx.application.Application;
import javafx.stage.Stage;

public class DatabaseUpdaterApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

	}

	/*
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
	 */

	/**
	 * Launch method for legacy java
	 *
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}
}

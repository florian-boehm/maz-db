package de.spiritaner.maz.util;

import org.apache.log4j.Logger;

import java.sql.*;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
public class UserDatabase {

	final static Logger logger = Logger.getLogger(UserDatabase.class);

	private static final String DATABASE_PATH = "./dbfiles/";
	private static final String USER_DATABASE_NAME = "users";
	private static final String USER_PASSWORD_TABLE_NAME = "USER_PASSWORD";

	/**
	 * Checks if the user database contains the user_password table with at least one user in it.
	 *
	 * @return	boolean	true if the user_password table exists and has at least one user; otherwise: false
	 */
	public static boolean isPopulated() {
		// Get a connection to the databse and let the jvm handle the close operation of it
		try (Connection conn = getConnection()) {
			// Get a statement object from the connection
			Statement st = conn.createStatement();
			// Search for the name of the user_password table in information_schema.tables
			ResultSet rs = st.executeQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '"+USER_PASSWORD_TABLE_NAME+"'");
			// Jump to the last element in the result set and get the row number
			rs.last();
			int rows = rs.getRow();

			// If there are no rows matching the previous query the user_password table does not exist
			if(0 == rows) {
				return false;
			} else {
				// Because the user_password table exists we check if there is at least one entry in it
				rs = st.executeQuery("SELECT * FROM "+USER_PASSWORD_TABLE_NAME+";");
				rs.last();

				return (rs.getRow() > 0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	private static Connection getConnection() throws ClassNotFoundException, SQLException {
		//logger.info("Getting a database connection");
		Class.forName("org.h2.Driver");
		return DriverManager.getConnection("jdbc:h2:"+DATABASE_PATH+USER_DATABASE_NAME, "anonymous", "");
	}
}

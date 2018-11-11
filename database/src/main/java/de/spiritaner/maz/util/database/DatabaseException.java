package de.spiritaner.maz.util.database;

/**
 * @author Florian Böhm
 * @version 2017.05.22
 */
public class DatabaseException extends Exception {

	public DatabaseException(String s) {
		super(s);
	}

	public DatabaseException(String s, Exception e) {
		super(s,e);
	}
}

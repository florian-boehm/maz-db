package de.spiritaner.maz;

import de.spiritaner.maz.dialog.LoginDialog;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
public class DatabaseApp extends Application {

	final static Logger logger = Logger.getLogger(DatabaseApp.class);

	@Override
	public void start(final Stage primaryStage) throws Exception {
		// If there are no users in the user database show the initialization dialog
		LoginDialog.show();
//		if(!UserDatabase.isPopulated()) {
//			System.out.println("not populated");
//		} else {
//			LoginDialog.populateStage(primaryStage);
//			primaryStage.show();
//		}
	}

	/**
	 * Launch method for legacy java
	 *
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}
}

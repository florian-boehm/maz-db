package de.spiritaner.maz;

import de.spiritaner.maz.dialog.LoginDialog;
import de.spiritaner.maz.util.UserDatabase;
import de.spiritaner.maz.view.InitView;
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
//		UserDatabase.createFirstUser("admin","admin");
//		ExceptionDialog.show(new Exception("Just a test"));

		if(!UserDatabase.isPopulated()) {
			// If there are no users in the user database show the database initialization dialog.
			InitView.populateStage(primaryStage);
			primaryStage.show();
		} else {
			LoginDialog.showWaitAndExitOnFailure(primaryStage);
		}
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

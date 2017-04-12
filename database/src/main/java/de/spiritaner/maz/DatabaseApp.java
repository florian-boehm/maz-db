package de.spiritaner.maz;

import de.spiritaner.maz.dialog.LoginDialog;
import de.spiritaner.maz.util.UserDatabase;
import de.spiritaner.maz.view.InitView;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.PrintStream;
import java.util.ResourceBundle;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
public class DatabaseApp extends Application {

	final static Logger logger = Logger.getLogger(DatabaseApp.class);

	@Override
	public void start(final Stage primaryStage) throws Exception {
		ResourceBundle guiText = ResourceBundle.getBundle("lang.gui");
		logger.info("Starting maz-db version "+guiText.getString("version"));

		// Redirect stdout and stderr to log4j
		System.setOut(createLoggingProxy(System.out));
		System.setErr(createLoggingProxy(System.err));

		try {
			if (!UserDatabase.isPopulated()) {
				// If there are no users in the user database show the database initialization dialog.
				InitView.populateStage(primaryStage);
				primaryStage.show();
			} else {
				LoginDialog.showWaitAndExitOnFailure(primaryStage);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	private PrintStream createLoggingProxy(PrintStream origStream) {
		return new PrintStream(origStream) {
			@Override
			public void print(final String string) {
				logger.info(string);
			}

			@Override
			public void println(final String string) {
				logger.info(string);
			}

			@Override
			public void println(final Object obj) {
				logger.info(obj);
			}
		};
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

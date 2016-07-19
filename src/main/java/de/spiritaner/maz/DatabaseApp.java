package de.spiritaner.maz;

import de.spiritaner.maz.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
public class DatabaseApp extends Application {

	@Override
	public void start(final Stage mainStage) throws Exception {
		LoginView.populateStage(mainStage);
		mainStage.show();
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

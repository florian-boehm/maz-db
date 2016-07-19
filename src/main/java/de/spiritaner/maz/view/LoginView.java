package de.spiritaner.maz.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
public class LoginView extends Scene {

	final static Logger logger = Logger.getLogger(LoginView.class);

	public static LoginView populateStage(Stage mainStage) {
		try {
			final FXMLLoader loader = new FXMLLoader(Scene.class.getClass().getResource("/fxml/login.fxml"));
			final Parent root = loader.load();

			final LoginView scene = new LoginView(root);
			mainStage.setScene(scene);
			mainStage.setTitle("MAZ-Datenbank Login");
			return scene;
		} catch (IOException e) {
			logger.error(e);
		}

		return null;
	}

	private LoginView(Parent root) {
		super(root,400,300);
	}
}

package de.spiritaner.maz.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by Florian on 7/19/2016.
 */
public class InitView extends Scene {

	final static Logger logger = Logger.getLogger(InitView.class);

	public static InitView populateStage(Stage primaryStage) {
		try {
			final FXMLLoader loader = new FXMLLoader(Scene.class.getClass().getResource("/fxml/init.fxml"));
			final Parent root = loader.load();

			final InitView scene = new InitView(root);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.sizeToScene();
			primaryStage.setTitle("MAZ-Datenbank");
			return scene;
		} catch (IOException e) {
			logger.error(e);
		}

		return null;
	}

	private InitView(Parent root) {
		super(root);
	}
}

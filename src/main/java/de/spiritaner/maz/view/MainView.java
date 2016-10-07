package de.spiritaner.maz.view;

import de.spiritaner.maz.controller.MainController;
import de.spiritaner.maz.dialog.ExceptionDialog;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by Florian on 8/13/2016.
 */
public class MainView extends Scene {

	final static Logger logger = Logger.getLogger(InitView.class);

	public static MainView populateStage(Stage primaryStage) {
		try {
			final FXMLLoader loader = new FXMLLoader(Scene.class.getClass().getResource("/fxml/main.fxml"));
			final Parent root = loader.load();
			final MainController controller = loader.getController();
			controller.setStage(primaryStage);

			final MainView scene = new MainView(root);
//			scene.getStylesheets().add(InitView.class.getClass().getResource("/css/validation.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.setResizable(true);
			primaryStage.sizeToScene();
			primaryStage.setMinHeight(600);
			primaryStage.setMinWidth(800);
			primaryStage.setTitle("MaZ-Datenbank");
			primaryStage.getIcons().add(new Image(InitView.class.getClass().getResource("/img/db_32.png").toString()));

			controller.loadAllPersons();
			return scene;
		} catch (IOException e) {
			ExceptionDialog.show(e);
		}

		return null;
	}

	private MainView(Parent root) {
		super(root);
	}
}

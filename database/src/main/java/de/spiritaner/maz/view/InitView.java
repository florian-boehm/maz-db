package de.spiritaner.maz.view;

import de.spiritaner.maz.controller.InitController;
import de.spiritaner.maz.dialog.ExceptionDialog;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by Florian on 7/19/2016.
 */
public class InitView extends Scene {

	final static Logger logger = Logger.getLogger(InitView.class);

	public static InitView populateStage(final Stage primaryStage) {
		try {
			final FXMLLoader loader = new FXMLLoader(Scene.class.getClass().getResource("/fxml/init.fxml"));
			final Parent root = loader.load();
			final InitController controller = loader.getController();
			controller.setStage(primaryStage);

			final InitView scene = new InitView(root);
//			scene.getStylesheets().add(InitView.class.getClass().getResource("/css/validation.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.setResizable(true);
			primaryStage.sizeToScene();
			primaryStage.setOnShown(event -> {
				primaryStage.setMaxHeight(primaryStage.getHeight());
				primaryStage.setMinHeight(primaryStage.getHeight());
				primaryStage.setMinWidth(primaryStage.getWidth());
			});
			primaryStage.setTitle("Einrichtung - MaZ-Datenbank");
			primaryStage.getIcons().add(new Image(InitView.class.getClass().getResource("/img/tools_48.png").toString()));
			return scene;
		} catch (IOException e) {
			ExceptionDialog.show(e);
		}

		return null;
	}

	private InitView(Parent root) {
		super(root);
	}
}

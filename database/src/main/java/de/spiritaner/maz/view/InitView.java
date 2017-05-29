package de.spiritaner.maz.view;

import de.spiritaner.maz.controller.InitController;
import de.spiritaner.maz.view.dialog.ExceptionDialog;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author Florian Schwab
 * @version 2017.05.29
 */
public class InitView extends Scene {

    final static Logger logger = Logger.getLogger(InitView.class);
    static InitController controller = null;

    public static InitView populateStage(final Stage stage) {
        try {
            final FXMLLoader loader = new FXMLLoader(Scene.class.getClass().getResource("/fxml/init.fxml"));
            final Parent root = loader.load();
            controller = loader.getController();
            controller.setStage(stage);

            final InitView scene = new InitView(root);
//			scene.getStylesheets().add(InitView.class.getClass().getResource("/css/validation.css").toExternalForm());

            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.sizeToScene();
            stage.setOnShown(event -> {
                stage.sizeToScene();
                stage.setMaxHeight(stage.getHeight());
                stage.setMinHeight(stage.getHeight());
                stage.setMinWidth(stage.getWidth());
            });
            stage.setTitle("Einrichtung - MaZ-Datenbank");
            stage.getIcons().add(new Image(InitView.class.getClass().getResource("/img/tools_48.png").toString()));
            return scene;
        } catch (IOException e) {
            ExceptionDialog.show(e);
        }

        return null;
    }

    private InitView(Parent root) {
        super(root);
    }

    public static InitController getController() {
        return controller;
    }
}

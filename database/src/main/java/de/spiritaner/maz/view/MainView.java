package de.spiritaner.maz.view;

import de.spiritaner.maz.controller.MainController;
import de.spiritaner.maz.view.dialog.ExceptionDialog;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author Florian Schwab
 * @version 2017.05.29
 */
public class MainView extends Scene {

    final static Logger logger = Logger.getLogger(InitView.class);

    public static MainView populateStage(Stage stage) {
        try {
            final FXMLLoader loader = new FXMLLoader(Scene.class.getClass().getResource("/fxml/main.fxml"));
            final Parent root = loader.load();
            final MainController controller = loader.getController();
            controller.setStage(stage);

            final MainView scene = new MainView(root);
//			scene.getStylesheets().add(InitView.class.getClass().getResource("/css/validation.css").toExternalForm());
            scene.getStylesheets().add(MainView.class.getClass().getResource("/css/side_tabs.css").toExternalForm());

            stage.setScene(scene);
            stage.setResizable(true);
            stage.sizeToScene();
            stage.setMinHeight(600);
            stage.setMinWidth(800);
            stage.setMaxHeight(4000);
            stage.setMaxWidth(4000);
            stage.setTitle("MaZ-Datenbank");
            stage.getIcons().clear();
            stage.getIcons().add(new Image(InitView.class.getClass().getResource("/img/db_32.png").toString()));

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

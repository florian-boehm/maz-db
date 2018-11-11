package de.spiritaner.maz.view.dialog;

import de.spiritaner.maz.controller.LoginController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * This dialog shows a username field, password field and selector for the current database directory.
 *
 * @author Florian Böhm
 * @version 2017.05.29
 */
public class LoginDialog extends Scene {

    final static Logger logger = Logger.getLogger(LoginDialog.class);

    // TODO copy this behavior to other populateStage methods, so that they return the controllers that are more usefull
    public static LoginController populateStage(final Stage stage) {
        try {
            final ResourceBundle guiText = ResourceBundle.getBundle("lang.gui");
            final FXMLLoader loader = new FXMLLoader(Scene.class.getClass().getResource("/fxml/login_dialog.fxml"));
            final Parent root = loader.load();
            final LoginController controller = loader.getController();
            final LoginDialog scene = new LoginDialog(root);

            stage.setTitle(guiText.getString("login_dialog_title"));
            stage.getIcons().add(new Image(LoginDialog.class.getClass().getResource("/img/login_32.png").toString()));
            stage.setResizable(true);
            stage.setScene(scene);
            stage.sizeToScene();

            // Load css if necessary
            // root.getStylesheets().add(EditorDialog.class.getClass().getResource("/css/login_dialog.css").toExternalForm());

            // Initialize the controller
            controller.setStage(stage);

            stage.setOnShown(event -> {
                Platform.runLater(() -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    stage.sizeToScene();
                    stage.setMaxHeight(stage.getHeight());
                    stage.setMinHeight(stage.getHeight());
                    stage.setMinWidth(stage.getWidth());
                });
            });

            return controller;
        } catch (IOException e) {
            ExceptionDialog.show(e);
        }

        return null;
    }

    private LoginDialog(Parent root) {
        super(root);
    }
}

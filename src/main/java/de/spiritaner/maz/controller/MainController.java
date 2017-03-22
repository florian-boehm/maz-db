package de.spiritaner.maz.controller;

import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.view.components.ImageTab;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Florian on 8/13/2016.
 */
public class MainController implements Initializable, Controller {

	@FXML private TabPane sideTabs;

	private HashMap<String, Controller> controllers;
	private Stage stage;

	public void initialize(URL location, ResourceBundle resources) {
	    if(controllers == null) controllers = new HashMap<>();

        sideTabs.getSelectionModel().clearSelection();

        sideTabs.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTab, newTab) -> {
            if(newTab instanceof ImageTab) {
                ImageTab imgTab = (ImageTab) newTab;

                try {
                    if (imgTab.getUrl() != null) {
                        if(imgTab.getContent() == null) {
                            final FXMLLoader loader = new FXMLLoader(new URL(imgTab.getUrl()));
                            final Parent root = loader.load();
                            final Controller controller = loader.getController();

                            // Save the controller for later usage
                            controllers.put(imgTab.getUrl(),controller);

                            controller.setStage(stage);
                            newTab.setContent(root);
                        } else {
                            //
                            controllers.get(imgTab.getUrl()).onReopen();
                        }
                    }
                } catch (IOException e) {
                    ExceptionDialog.show(e);
                }
            }
        });

        sideTabs.getSelectionModel().selectFirst();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

    @Override
    public void onReopen() {
        
    }

    public void closeApplication(ActionEvent actionEvent) {
		Platform.exit();
	}

	public void showVersion(ActionEvent actionEvent) {
		Locale.setDefault(new Locale("de","DE"));
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("Look, an Information Dialog");
		ResourceBundle guiText = ResourceBundle.getBundle("lang.gui");
		alert.setContentText(guiText.getString("version"));

		alert.showAndWait();
	}
}

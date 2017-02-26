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
import java.util.ResourceBundle;

/**
 * Created by Florian on 8/13/2016.
 */
public class MainController implements Initializable, Controller {

	@FXML private TabPane sideTabs;

	private Stage stage;

	public void initialize(URL location, ResourceBundle resources) {
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

                            controller.setStage(stage);
                            newTab.setContent(root);
                        } else {
                            // TODO what to do if the tab is already loaded?
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

	public void closeApplication(ActionEvent actionEvent) {
		Platform.exit();
	}
}

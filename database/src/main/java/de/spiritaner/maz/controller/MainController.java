package de.spiritaner.maz.controller;

import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.dialog.OverviewDialog;
import de.spiritaner.maz.dialog.UpdaterDialog;
import de.spiritaner.maz.view.component.ImageTab;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController implements Initializable, Controller {

	@FXML private TabPane sideTabs;

	private HashMap<String, Controller> controllers;
	private Stage stage;

	public void initialize(URL location, ResourceBundle resources) {
		if (controllers == null) controllers = new HashMap<>();

		sideTabs.getSelectionModel().clearSelection();

		sideTabs.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTab, newTab) -> {
			if (newTab instanceof ImageTab) {
				ImageTab imgTab = (ImageTab) newTab;

				try {
					if (imgTab.getUrl() != null) {
						if (imgTab.getContent() == null) {
							final FXMLLoader loader = new FXMLLoader(new URL(imgTab.getUrl()));
							final Parent root = loader.load();
							final Controller controller = loader.getController();

							if(imgTab.getUrl().contains("document_page.fxml"))
								root.getStylesheets().add(OverviewDialog.class.getClass().getResource("/css/wizard_tabs.css").toExternalForm());

							// Save the controller for later usage
							controllers.put(imgTab.getUrl(), controller);

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
		UpdaterDialog.showAndWait(stage);
	}
}

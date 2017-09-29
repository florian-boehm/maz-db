package de.spiritaner.maz.controller;

import de.spiritaner.maz.model.User;
import de.spiritaner.maz.view.dialog.ExceptionDialog;
import de.spiritaner.maz.view.dialog.OverviewDialog;
import de.spiritaner.maz.view.component.ImageTab;
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

	public void showLicense(ActionEvent actionEvent) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("MaZ-Datenbank");
		alert.setHeaderText("Informationen zur MaZ-Datebank");
		alert.setContentText("Copyright (C) 2017 Florian Schwab\n" +
				  "\n" +
				  "This program is free software: you can redistribute it and/or \nmodify" +
				  "it under the terms of the GNU General Public License\nas published by" +
				  "the Free Software Foundation, either version 3 of\n the License, or" +
				  "(at your option) any later version.\n" +
				  "\n" +
				  "This program is distributed in the hope that it will be useful,\n" +
				  "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
				  "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the\n" +
				  "GNU General Public License for more details.\n" +
				  "\n" +
				  "You should have received a copy of the GNU General Public License\n" +
				  "along with this program. If not, see <http://www.gnu.org/licenses/>.");

		alert.showAndWait();
	}

	public void showUserDialog(ActionEvent actionEvent) {
		final OverviewDialog<UserOverviewController, User> overviewDialog = new OverviewDialog<>(UserOverviewController.class);
		overviewDialog.showUsers(stage);
	}
}

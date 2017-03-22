package de.spiritaner.maz.dialog;

import de.spiritaner.maz.controller.OverviewController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class OverviewDialog<T extends OverviewController, K> {

	private Class<T> cls;

	public OverviewDialog(Class<T> cls) {
		this.cls = cls;
	}

	public Optional<K> showAndWait(Stage stage) {
		OverviewController.Annotation controllerAnnotation = cls.getAnnotation(OverviewController.Annotation.class);
		Dialog<K> dialog = new Dialog<>();

		dialog.setTitle("Auswahlübersicht");
		dialog.setHeaderText("Bitte wählen Sie ein(e) "+controllerAnnotation.objDesc()+" aus ...");
		dialog.getDialogPane().setPadding(new Insets(0, 0, 0, 0));

		// Set the icon (must be included in the project).
		//dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

		// Set the button types.
		ButtonType selectButtonType = new ButtonType("Auswählen", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);

		if (!controllerAnnotation.fxmlFile().isEmpty()) {
			try {
				final FXMLLoader loader = new FXMLLoader(Scene.class.getClass().getResource(controllerAnnotation.fxmlFile()));
				final Parent root = loader.load();
				final T controller = loader.getController();
				root.getStylesheets().add(OverviewDialog.class.getClass().getResource("/css/overview_dialog.css").toExternalForm());
				controller.setStage(stage);
				dialog.getDialogPane().setContent(root);

				// Enable/Disable login button depending on whether a username was entered.
				final Node selectButton = dialog.getDialogPane().lookupButton(selectButtonType);
				selectButton.setDisable(true);

				// Enable selection on double clicking a row
				controller.setEditOnDoubleclick(false);
				controller.getTable().setRowFactory(tv -> {
					TableRow<T> row = new TableRow<>();

					row.setOnMouseClicked(event -> {
						if (event.getClickCount() == 2 && (!row.isEmpty())) {
							((Button) selectButton).fire();
						}
					});

					return row;
				});

				controller.setToolbarVisible(false);

				// Do some validation (using the Java 8 lambda syntax).
				controller.getTable().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
					selectButton.setDisable(newValue == null);
				});

				// Convert the result to a username-password-pair when the login button is clicked.
				dialog.setResultConverter(dialogButton -> {
					if (dialogButton == selectButtonType) {
						return (K) controller.getTable().getSelectionModel().getSelectedItem();
					}
					return null;
				});

				return dialog.showAndWait();
			} catch (IOException e) {
				ExceptionDialog.show(e);
			}
		}

		return Optional.empty();
	}
}

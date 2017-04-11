package de.spiritaner.maz.dialog;

import de.spiritaner.maz.controller.ProgressController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.List;

public class ProgressDialog {

	private Stage stage;
	private ProgressController controller;

	private ProgressDialog(Stage parent) {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/progress_dialog.fxml"));
			Parent root = loader.load();

			this.stage = new Stage();
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(parent);
			stage.setTitle("Update-Fortschritt");
			stage.setResizable(true);
			stage.setScene(new Scene(root));
			stage.sizeToScene();

			controller = loader.getController();
			controller.setStage(stage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startUpdate() {
		stage.show();

		Platform.setImplicitExit(false);

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				event.consume();
			}
		});

		controller.startUpdate();

		Platform.setImplicitExit(true);
	}

	public ProgressController getController() {
		return controller;
	}

	public static void startUpdate(Stage stage, String releaseJsonString) {
		ProgressDialog progressDialog = new ProgressDialog(stage);
		progressDialog.getController().setReleaseJsonString(releaseJsonString);
		progressDialog.startUpdate();
	}
}

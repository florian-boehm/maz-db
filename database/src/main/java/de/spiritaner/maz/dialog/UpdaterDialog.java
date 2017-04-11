package de.spiritaner.maz.dialog;

import de.spiritaner.maz.controller.UpdaterController;
import javafx.beans.NamedArg;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

public class UpdaterDialog {

	private Stage stage;
	private UpdaterController controller;

	final static Logger logger = Logger.getLogger(UpdaterDialog.class);

	private UpdaterDialog(Stage parent) {
		try {
			final FXMLLoader loader = new FXMLLoader(Scene.class.getClass().getResource("/fxml/updater.fxml"));
			final Parent root = loader.load();
			final UpdaterController controller = loader.getController();
			stage = new Stage();
			stage.initOwner(parent);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setTitle("Version/Updates");
			stage.setScene(new Scene(root));
			stage.setResizable(true);
			stage.sizeToScene();

			controller.setStage(stage);

			//primaryStage.getIcons().add(new Image(InitView.class.getClass().getResource("/img/db_32.png").toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public UpdaterController getController() {
		return controller;
	}

	public void setController(UpdaterController controller) {
		this.controller = controller;
	}

	public static void showAndWait(Stage parent) {
		UpdaterDialog updaterDialog = new UpdaterDialog(parent);
		updaterDialog.showAndWait();
	}

	private void showAndWait() {
		stage.showAndWait();
	}
}

package de.spiritaner.maz.dialog;

import de.spiritaner.maz.controller.residence.ResidenceEditorDialogController;
import de.spiritaner.maz.model.Residence;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ResidenceEditorDialog extends Scene {

	public static boolean showAndWait(Residence residence, Stage primaryStage) {
		try {
			final FXMLLoader loader = new FXMLLoader(Scene.class.getClass().getResource("/fxml/residence/residence_editor_dialog.fxml"));
			final Parent root = loader.load();
			final ResidenceEditorDialogController controller = loader.getController();

			Stage dialogStage = new Stage();
			dialogStage.setTitle((residence == null || residence.getId() == 0L) ? "Wohnort anlegen" : "Wohnort bearbeiten");
			dialogStage.initOwner(primaryStage);
			// TODO Is the modality of this window really necessary
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.setResizable(true);
			dialogStage.sizeToScene();
			// TODO Find a way to set the maximum window size correctly
			//dialogStage.setOnShown(event -> {
			//	dialogStage.sizeToScene();
			//	dialogStage.setMaxHeight(dialogStage.getHeight());
			//	dialogStage.setMinHeight(dialogStage.getHeight());
			//	dialogStage.setMinWidth(dialogStage.getWidth());
			//});

			controller.setResidence(residence);
			controller.setStage(dialogStage);

			ResidenceEditorDialog personEditorDialog = new ResidenceEditorDialog(root);
			dialogStage.setScene(personEditorDialog);
			dialogStage.showAndWait();

			return true;
		}  catch (IOException e) {
			ExceptionDialog.show(e);
			return false;
		}
	}

	private ResidenceEditorDialog(Parent root) {
		super(root);
	}
}

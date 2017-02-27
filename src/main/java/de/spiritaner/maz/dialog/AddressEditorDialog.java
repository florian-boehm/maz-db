package de.spiritaner.maz.dialog;

import de.spiritaner.maz.controller.AddressEditorController;
import de.spiritaner.maz.controller.PersonEditorController;
import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.model.Person;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AddressEditorDialog extends Scene {

	public static boolean showAndWait(Address address, Stage primaryStage) {
		try {
			final FXMLLoader loader = new FXMLLoader(Scene.class.getClass().getResource("/fxml/address_editor.fxml"));
			final Parent root = loader.load();
			final AddressEditorController controller = loader.getController();

			Stage dialogStage = new Stage();
			dialogStage.setTitle((address == null) ? "Adresse anlegen" : "Adresse bearbeiten");
			dialogStage.initOwner(primaryStage);
			// TODO Is the modality of this window really necessary
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.setResizable(true);
			dialogStage.sizeToScene();
			// TODO Find a way to set the maximum window size correctly
			//dialogStage.setOnShown(event -> {
			//	dialogStage.setMaxHeight(dialogStage.getHeight());
			//	dialogStage.setMinHeight(dialogStage.getHeight());
			//	dialogStage.setMinWidth(dialogStage.getWidth());
			//});

			controller.setAddress(address);
			controller.setStage(dialogStage);

			AddressEditorDialog personEditorDialog = new AddressEditorDialog(root);
			dialogStage.setScene(personEditorDialog);
			dialogStage.showAndWait();

			return true;
		}  catch (IOException e) {
			ExceptionDialog.show(e);
			return false;
		}
	}

	private AddressEditorDialog(Parent root) {
		super(root);
	}
}

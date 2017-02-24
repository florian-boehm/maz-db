package de.spiritaner.maz.dialog;

import de.spiritaner.maz.controller.PersonEditorController;
import de.spiritaner.maz.model.Person;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Florian on 9/26/2016.
 */
public class PersonEditorDialog extends Scene {

	public static boolean showAndWait(Person person, Stage primaryStage) {
		try {
			final FXMLLoader loader = new FXMLLoader(Scene.class.getClass().getResource("/fxml/person_editor.fxml"));
			final Parent root = loader.load();
			final PersonEditorController controller = loader.getController();

			Stage dialogStage = new Stage();
			dialogStage.setTitle((person == null) ? "Neue Person anlegen" : "Person bearbeiten");
			dialogStage.initOwner(primaryStage);
			// TODO Is the modality of this window really necessary
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.setResizable(true);
			dialogStage.sizeToScene();
			dialogStage.setOnShown(event -> {
				dialogStage.setMaxHeight(dialogStage.getHeight());
				dialogStage.setMinHeight(dialogStage.getHeight());
				dialogStage.setMinWidth(dialogStage.getWidth());
			});

			controller.setPerson(person);
			controller.setStage(dialogStage);
			controller.loadGender();

			PersonEditorDialog personEditorDialog = new PersonEditorDialog(root);
			dialogStage.setScene(personEditorDialog);
			dialogStage.showAndWait();

			return true;
		}  catch (IOException e) {
			ExceptionDialog.show(e);
			return false;
		}
	}

	private PersonEditorDialog(Parent root) {
		super(root);
	}
}

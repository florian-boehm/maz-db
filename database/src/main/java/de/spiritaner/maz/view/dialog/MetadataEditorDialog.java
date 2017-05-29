package de.spiritaner.maz.view.dialog;

import de.spiritaner.maz.model.meta.MetaClass;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.StageStyle;

import java.util.Optional;

public class MetadataEditorDialog {

	public static Optional<String> showAndWait(MetaClass metaClassObj, String metaName) {
		TextInputDialog dialog;

		if (metaClassObj == null) {
			dialog = new TextInputDialog();
			dialog.setTitle(metaName + " anlegen");
		} else {
			dialog = new TextInputDialog(metaClassObj.getDescription());
			dialog.setTitle(metaName + " bearbeiten");
		}

		Node submitButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
		submitButton.setDisable(dialog.getEditor().getText().trim().isEmpty());

		dialog.getEditor().textProperty().addListener((observable, oldValue, newValue) ->
				  submitButton.setDisable(newValue.trim().isEmpty()));

		dialog.initStyle(StageStyle.UTILITY);
		dialog.setHeaderText(null);
		dialog.setContentText("Beschreibung: ");

		return dialog.showAndWait();
	}
}

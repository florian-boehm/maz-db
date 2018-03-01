package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.view.dialog.EditorDialog;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/participation/event_editor_dialog.fxml", objDesc = "$event")
public class EventEditorDialogController extends EditorDialogController<Event> {

	public GridPane eventEditor;
	public EventEditorController eventEditorController;

	@Override
	protected void bind(Event event) {
		eventEditorController.event.bindBidirectional(identifiable);
	}

	@Override
	protected boolean allValid() {
		return eventEditorController.isValid();
	}
}

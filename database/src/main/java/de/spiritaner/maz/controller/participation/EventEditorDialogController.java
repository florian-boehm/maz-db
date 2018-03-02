package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.view.dialog.EditorDialog;
import javafx.scene.layout.GridPane;

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

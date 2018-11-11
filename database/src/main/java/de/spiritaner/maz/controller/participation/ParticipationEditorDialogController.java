package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.controller.person.PersonOverviewController;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.Participation;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.view.dialog.EditorDialog;
import de.spiritaner.maz.view.dialog.OverviewDialog;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.apache.log4j.Logger;

import java.util.Optional;

@EditorDialog.Annotation(fxmlFile = "/fxml/participation/participation_editor_dialog.fxml", objDesc = "$participation")
public class ParticipationEditorDialogController extends EditorDialogController<Participation> {

	final static Logger logger = Logger.getLogger(ParticipationEditorDialogController.class);

	public GridPane eventEditor;
	public EventEditorController eventEditorController;
	public GridPane personEditor;
	public PersonEditorController personEditorController;
	public GridPane participationEditor;
	public ParticipationEditorController participationEditorController;
	public Button searchEventButton;
	public Button searchPersonButton;

	@Override
	protected void bind(Participation participation) {
		participationEditorController.participation.bindBidirectional(identifiable);

		eventEditorController.event.bindBidirectional(participation.event);
		eventEditorController.readOnly.bind(participation.event.isNotNull());
		searchEventButton.disableProperty().bind(Bindings.createBooleanBinding(() -> participation.event.get().id.get() == 0L, participation.event));

		personEditorController.person.bindBidirectional(participation.person);
		//personEditorController.readOnly.bind(Bindings.createBooleanBinding(() -> participation.event.get().id.get() == 0L, participation.person));
		personEditorController.readOnly.bind(participation.person.isNotNull());
		searchPersonButton.disableProperty().bind(Bindings.createBooleanBinding(() -> participation.event.get().id.get() == 0L, participation.event));
	}

	@Override
	protected boolean allValid() {
		boolean personValid = personEditorController.isValid();
		boolean eventValid = eventEditorController.isValid();
		boolean participationValid = participationEditorController.isValid();

		return personValid && eventValid && participationValid;
	}

	public void searchEvent(final ActionEvent actionEvent) {
		OverviewDialog<EventOverviewController, Event> dialog = new OverviewDialog<>(EventOverviewController.class);
		Optional<Event> result = dialog.showAndSelect(getStage());

		result.ifPresent((Event event) -> identifiable.get().event.set(event));
	}

	public void searchPerson(final ActionEvent actionEvent) {
		OverviewDialog<PersonOverviewController, Person> dialog = new OverviewDialog<>(PersonOverviewController.class);
		Optional<Person> result = dialog.showAndSelect(getStage());

		result.ifPresent((Person person) -> identifiable.get().person.set(person));
	}
}

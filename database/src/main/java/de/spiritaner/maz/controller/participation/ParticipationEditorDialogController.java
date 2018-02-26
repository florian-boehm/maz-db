package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.controller.person.PersonOverviewController;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.Participation;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.view.dialog.EditorDialog;
import de.spiritaner.maz.view.dialog.OverviewDialog;
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
import java.util.Optional;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/participation/participation_editor_dialog.fxml", objDesc = "Teilnahme")
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
	public void setIdentifiable(Participation participation) {
		super.setIdentifiable(participation);

		if (participation != null) {
			if (participation.getEvent() != null) {
				eventEditorController.setAll(participation.getEvent());
				searchEventButton.setDisable(true);
			}

			if (participation.getPerson() != null) {
				personEditorController.person.set(participation.getPerson());
				searchPersonButton.setDisable(true);
			}

			eventEditorController.setReadonly(true);
			personEditorController.readOnly.set(true);
			participationEditorController.setAll(participation);
		}
	}

	public void saveParticipant(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			boolean personValid = personEditorController.isValid();
			boolean eventValid = eventEditorController.isValid();
			boolean participationValid = participationEditorController.isValid();

			if (personValid && eventValid && participationValid) {
				EntityManager em = CoreDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				getIdentifiable().setEvent(eventEditorController.getAll(getIdentifiable().getEvent()));
				participationEditorController.getAll(getIdentifiable());

				try {
					Participation managedParticipation = (!em.contains(getIdentifiable())) ? em.merge(getIdentifiable()) : getIdentifiable();
					em.getTransaction().commit();

					//if(managedParticipation != null) {
					//	if(!getIdentifiable().getPerson().getParticipations().contains(managedParticipation)) getIdentifiable().getPerson().getParticipations().add(managedParticipation);
					//	if(!getIdentifiable().getEvent().getParticipations().contains(managedParticipation)) getIdentifiable().getEvent().getParticipations().add(managedParticipation);
					//}

					setResult(managedParticipation);
					requestClose();
				} catch (PersistenceException e) {
					em.getTransaction().rollback();
					logger.warn(e);
				} finally {
					em.close();
				}
			}
		});
	}

	public void searchEvent(ActionEvent actionEvent) {
		OverviewDialog<EventOverviewController, Event> dialog = new OverviewDialog<>(EventOverviewController.class);
		Optional<Event> result = dialog.showAndSelect(getStage());

		result.ifPresent((Event event) -> {
			getIdentifiable().setEvent(event);
			eventEditorController.setAll(event);
		});
	}

	public void searchPerson(ActionEvent actionEvent) {
		OverviewDialog<PersonOverviewController, Person> dialog = new OverviewDialog<>(PersonOverviewController.class);
		Optional<Person> result = dialog.showAndSelect(getStage());

		result.ifPresent((Person person) -> {
			getIdentifiable().setPerson(person);
			personEditorController.person.set(person);
		});
	}
}

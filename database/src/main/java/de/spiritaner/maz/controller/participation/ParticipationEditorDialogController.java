package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.controller.person.PersonOverviewController;
import de.spiritaner.maz.view.dialog.EditorDialog;
import de.spiritaner.maz.view.dialog.OverviewDialog;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.Participation;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.util.database.CoreDatabase;
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
public class ParticipationEditorDialogController extends EditorController<Participation> {

	final static Logger logger = Logger.getLogger(ParticipationEditorDialogController.class);

	@FXML
	private Button saveParticipantButton;
	@FXML
	private Text titleText;
	@FXML
	private GridPane eventEditor;
	@FXML
	private EventEditorController eventEditorController;
	@FXML
	private GridPane personEditor;
	@FXML
	private PersonEditorController personEditorController;
	@FXML
	private GridPane participationEditor;
	@FXML
	private ParticipationEditorController participationEditorController;
	@FXML
	private Button searchEventButton;
	@FXML
	private Button searchPersonButton;

	@Override
	public void setIdentifiable(Participation participation) {
		super.setIdentifiable(participation);

		if (participation != null) {
			if (participation.getEvent() != null) {
				eventEditorController.setAll(participation.getEvent());
				searchEventButton.setDisable(true);
			}

			if (participation.getPerson() != null) {
				personEditorController.setAll(participation.getPerson());
				searchPersonButton.setDisable(true);
			}

			eventEditorController.setReadonly(true);
			personEditorController.setReadonly(true);
			participationEditorController.setAll(participation);

			if (participation.getId() != 0L) {
				titleText.setText(getIdentifiableName() + " bearbeiten");
				saveParticipantButton.setText("Speichern");
			} else {
				titleText.setText(getIdentifiableName() + " anlegen");
				saveParticipantButton.setText("Anlegen");
			}
		}
	}

	@Override
	public void onReopen() {
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

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
				getIdentifiable().setPerson(personEditorController.getAll(getIdentifiable().getPerson()));
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

	public void closeDialog(ActionEvent actionEvent) {
		Platform.runLater(() -> getStage().close());
	}

	public void searchEvent(ActionEvent actionEvent) {
		OverviewDialog<EventOverviewController, Event> dialog = new OverviewDialog<>(EventOverviewController.class);
		Optional<Event> result = dialog.showAndWait(getStage());

		result.ifPresent((Event event) -> {
			getIdentifiable().setEvent(event);
			eventEditorController.setAll(event);
		});
	}

	public void searchPerson(ActionEvent actionEvent) {
		OverviewDialog<PersonOverviewController, Person> dialog = new OverviewDialog<>(PersonOverviewController.class);
		Optional<Person> result = dialog.showAndWait(getStage());

		result.ifPresent((Person person) -> {
			getIdentifiable().setPerson(person);
			personEditorController.setAll(person);
		});
	}
}

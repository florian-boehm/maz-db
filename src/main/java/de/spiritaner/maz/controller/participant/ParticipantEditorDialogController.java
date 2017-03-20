package de.spiritaner.maz.controller.participant;

import de.spiritaner.maz.controller.Controller;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.controller.person.PersonOverviewController;
import de.spiritaner.maz.dialog.OverviewDialog;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.Participant;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.util.DataDatabase;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ParticipantEditorDialogController implements Initializable, Controller {

	final static Logger logger = Logger.getLogger(ParticipantEditorDialogController.class);

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
	private GridPane participantEditor;
	@FXML
	private ParticipantEditorController participantEditorController;
	@FXML
	private Button searchEventButton;
	@FXML
	private Button searchPersonButton;

	private Stage stage;
	private Participant participant;

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void onReopen() {
	}

	public void setParticipant(Participant participant) {
		this.participant = participant;

		if (participant != null) {
			if (participant.getEvent() != null) {
				eventEditorController.setAll(participant.getEvent());
				searchEventButton.setDisable(true);
			}

			if (participant.getPerson() != null) {
				personEditorController.setAll(participant.getPerson());
				searchPersonButton.setDisable(true);
			}

			eventEditorController.setReadonly(true);
			personEditorController.setReadonly(true);
			participantEditorController.setAll(participant);

			if (participant.getId() != 0L) {
				titleText.setText("Teilnehmer bearbeiten");
				saveParticipantButton.setText("Speichern");
			} else {
				titleText.setText("Teilnehmer anlegen");
				saveParticipantButton.setText("Anlegen");
			}
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}

	public void saveParticipant(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			boolean personValid = personEditorController.isValid();
			boolean eventValid = eventEditorController.isValid();
			boolean participantValid = participantEditorController.isValid();

			if (personValid && eventValid && participantValid) {
				EntityManager em = DataDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				participant.setEvent(eventEditorController.getAll(participant.getEvent()));
				participant.setPerson(personEditorController.getAll(participant.getPerson()));
				participantEditorController.getAll(participant);

				try {
					if (!em.contains(participant)) em.merge(participant);

					em.getTransaction().commit();
					stage.close();
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
		Platform.runLater(() -> stage.close());
	}

	public void searchEvent(ActionEvent actionEvent) {
		OverviewDialog<EventOverviewController, Event> dialog = new OverviewDialog<>(EventOverviewController.class);
		Optional<Event> result = dialog.showAndWait(stage);

		result.ifPresent((Event event) -> {
			participant.setEvent(event);
			eventEditorController.setAll(event);
		});
	}

	public void searchPerson(ActionEvent actionEvent) {
		OverviewDialog<PersonOverviewController, Person> dialog = new OverviewDialog<>(PersonOverviewController.class);
		Optional<Person> result = dialog.showAndWait(stage);

		result.ifPresent((Person person) -> {
			participant.setPerson(person);
			personEditorController.setAll(person);
		});
	}
}

package de.spiritaner.maz.controller.participant;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.Participant;
import de.spiritaner.maz.model.Person;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

public class ParticipantOverviewController extends OverviewController<Participant> {

	@FXML private TableColumn<Participant, String> eventColumn;
	@FXML private TableColumn<Participant, String> personColumn;
	@FXML private TableColumn<Participant, String> participantTypeColumn;
	@FXML private TableColumn<Participant, String> participatedColumn;
	@FXML private TableColumn<Participant, Long> idColumn;

	// This controller can be person or participant centric
	private Person person;
	private Event event;

	public ParticipantOverviewController() {
		super(Participant.class);
	}

	public void setPerson(Person person) {
		personColumn.setVisible(person == null);
		this.person = person;
	}

	public void setEvent(Event event) {
		eventColumn.setVisible(event == null);
		this.event = event;
	}

	@Override
	public void preCreate(Participant participant) {
		participant.setEvent(event);
		participant.setPerson(person);
	}

	@Override
	public void preEdit(final Participant participant) {
		//participant.setPerson(person);
		//participant.setParticipant(participant);
	}

	@Override
	protected void preRemove(Participant obsoleteEntity) {

	}

	@Override
	protected void handleException(RollbackException e) {
		ExceptionDialog.show(e);
	}

	@Override
	protected String getLoadingText() {
		if (person != null && event == null) {
			return "Lade Teilnahmen ...";
		}

		return "Lade Teilnehmer ...";
	}

	@Override
	protected Collection<Participant> preLoad(EntityManager em) {
		if (person != null && event == null) {
			Hibernate.initialize(person.getParticipations());
			return FXCollections.observableArrayList(person.getParticipations());
		} else if(person == null && event != null) {
			Hibernate.initialize(event.getParticipants());
			return FXCollections.observableArrayList(event.getParticipants());
		}

		return FXCollections.emptyObservableList();
	}

	@Override
	protected void postLoad(Collection<Participant> loadedObjs) {

	}

	@Override
	public void preInit() {
		eventColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEvent().toString()));
		personColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPerson().toString()));
		participantTypeColumn.setCellValueFactory(cellData -> cellData.getValue().getParticipantType().descriptionProperty());
		participatedColumn.setCellValueFactory(cellData -> cellData.getValue().hasParticipatedStringProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
	}

	@Override
	public void postInit() {

	}
}

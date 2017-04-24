package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.dialog.RemoveDialog;
import de.spiritaner.maz.model.ContactMethod;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.Participation;
import de.spiritaner.maz.model.Person;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

public class ParticipationOverviewController extends OverviewController<Participation> {

	@FXML private TableColumn<Participation, String> eventColumn;
	@FXML private TableColumn<Participation, String> personColumn;
	@FXML private TableColumn<Participation, String> participantTypeColumn;
	@FXML private TableColumn<Participation, String> participatedColumn;
	@FXML private TableColumn<Participation, Long> idColumn;

	// This controller can be person or participation centric
	private Person person;
	private Event event;

	public ParticipationOverviewController() {
		super(Participation.class, true);
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
	public void preCreate(Participation participation) {
		participation.setEvent(event);
		participation.setPerson(person);
	}

	@Override
	protected void postCreate(Participation participation) {

	}

	@Override
	public void preEdit(final Participation participation) {
		//participation.setPerson(person);
		//participation.setParticipation(participation);
	}

	@Override
	protected void preRemove(Participation obsoleteEntity, EntityManager em) {

	}

	@Override
	protected void handleException(RollbackException e, Participation participation) {
		RemoveDialog.showFailureAndWait("Teilnahme","Teilnahme von '"+participation.getPerson().getFullName()+"' an '"+participation.getEvent().getName()+"'", e);
	}

	@Override
	protected String getLoadingText() {
		if (person != null && event == null) {
			return "Lade Teilnahmen ...";
		}

		return "Lade Teilnehmer ...";
	}

	@Override
	protected Collection<Participation> preLoad(EntityManager em) {
		if (person != null && event == null) {
			Hibernate.initialize(person.getParticipations());
			return FXCollections.observableArrayList(person.getParticipations());
		} else if(person == null && event != null) {
			Hibernate.initialize(event.getParticipations());
			return FXCollections.observableArrayList(event.getParticipations());
		}

		return FXCollections.emptyObservableList();
	}

	@Override
	protected void postLoad(Collection<Participation> loadedObjs) {

	}

	@Override
	public void preInit() {
		eventColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEvent().toString()));
		personColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPerson().toString()));
		participantTypeColumn.setCellValueFactory(cellData -> cellData.getValue().getParticipationType().descriptionProperty());
		participatedColumn.setCellValueFactory(cellData -> cellData.getValue().hasParticipatedStringProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
	}

	@Override
	public void postInit() {

	}

	@Override
	public void postRemove(Participation obj) {
		if(person != null) person.getParticipations().remove(obj);
		if(event != null) event.getParticipations().remove(obj);
	}
}

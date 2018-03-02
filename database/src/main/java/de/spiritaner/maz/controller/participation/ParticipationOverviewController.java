package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.Participation;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.meta.ParticipationType;
import de.spiritaner.maz.view.dialog.RemoveDialog;
import de.spiritaner.maz.view.renderer.BooleanTableCell;
import de.spiritaner.maz.view.renderer.MetaClassTableCell;
import javafx.collections.FXCollections;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

public class ParticipationOverviewController extends OverviewController<Participation> {

	public TableColumn<Participation, Event> eventColumn;
	public TableColumn<Participation, Person> personColumn;
	public TableColumn<Participation, ParticipationType> participantTypeColumn;
	public TableColumn<Participation, Boolean> participatedColumn;

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
		eventColumn.setCellValueFactory(cellData -> cellData.getValue().eventProperty());
		personColumn.setCellValueFactory(cellData -> cellData.getValue().personProperty());
		participantTypeColumn.setCellValueFactory(cellData -> cellData.getValue().participationTypeProperty());
		participatedColumn.setCellValueFactory(cellData -> cellData.getValue().hasParticipatedProperty());

		participantTypeColumn.setCellFactory(column -> new MetaClassTableCell<>());
		participatedColumn.setCellFactory(column -> new BooleanTableCell<>());
		eventColumn.setCellFactory(column -> new TableCell<Participation, Event>() {
			@Override
			public  void updateItem(Event item, boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					setText(item.getName());
				}
			}
		});
		personColumn.setCellFactory(column -> new TableCell<Participation, Person>() {
			@Override
			public  void updateItem(Person item, boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					setText(item.getFullName());
				}
			}
		});
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

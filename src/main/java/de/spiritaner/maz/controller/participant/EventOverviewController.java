package de.spiritaner.maz.controller.participant;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.dialog.RemoveDialog;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.Participant;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.util.DataDatabase;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class EventOverviewController extends OverviewController<Event> {

	@FXML private TableColumn<Event, String> eventTypeColumn;
	@FXML private TableColumn<Event, String> nameColumn;
	@FXML private TableColumn<Event, String> descriptionColumn;
	@FXML private TableColumn<Event, LocalDate> fromDateColumn;
	@FXML private TableColumn<Event, LocalDate> toDateColumn;
	@FXML private TableColumn<Event, Long> idColumn;

	private Stage stage;

	public EventOverviewController() {
		super(Event.class);
	}

	@Override
	public void preCreate(Event object) {

	}

	@Override
	public void preEdit(Event object) {

	}

	@Override
	protected void preRemove(Event obsoleteEntity) {

	}

	@Override
	protected void handleException(RollbackException e) {
		ExceptionDialog.show(e);
	}

	@Override
	protected String getLoadingText() {
		return "Lade Veranstaltungen ...";
	}

	@Override
	protected void postLoad(Collection<Event> loadedObjs) {

	}

	@Override
	protected Collection<Event> preLoad(EntityManager em) {
		return em.createNamedQuery("Event.findAll", Event.class).getResultList();
	}

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	protected void preInit() {

	}

	@Override
	protected void postInit() {
		eventTypeColumn.setCellValueFactory(cellData -> cellData.getValue().getEventType().descriptionProperty());
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		// TODO Implement correct date formatting like in person overview controller
		fromDateColumn.setCellValueFactory(cellData -> cellData.getValue().fromDateProperty());
		toDateColumn.setCellValueFactory(cellData -> cellData.getValue().toDateProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
	}

	/*public void removeEvent(ActionEvent actionEvent) {
		Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
		final Optional<ButtonType> result = RemoveDialog.create(selectedEvent, stage).showAndWait();

		if (result.get() == ButtonType.OK) {
			try {
				EntityManager em = DataDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();
				Event obsoleteEvent = em.find(Event.class, selectedEvent.getId());
				em.remove(obsoleteEvent);
				//TODO this is not possible here
				// person..remove(selectedEvent);
				em.getTransaction().commit();

				loadEventsForPerson();
			} catch (RollbackException e) {
				// TODO show graphical error message in better way
				ExceptionDialog.show(e);
			}
		}
	}*/

	/*public void editEvent(ActionEvent actionEvent) {
		Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
		EditorDialog.showAndWait(selectedEvent, stage);

		loadEventsForPerson();
	}*/

	/*public void loadEventsForPerson() {
		if (person != null) {
			masker.setProgressVisible(true);
			masker.setText("Lade Veranstaltungen ...");
			masker.setVisible(true);

			new Thread(new Task() {
				@Override
				protected Collection<Event> call() throws Exception {
					EntityManager em = DataDatabase.getFactory().createEntityManager();
					em.getTransaction().begin();
					Hibernate.initialize(person.getParticipations());
					em.getTransaction().commit();
					HashSet<Event> events = new HashSet<>();

					for(Participant participation : person.getParticipations()) {
						events.add(participation.getEvent());
					}

					Collection<Event> result = FXCollections.observableArrayList(events);

					eventTable.getItems().clear();
					eventTable.getItems().addAll(result);
					masker.setVisible(false);
					return result;
				}
			}).start();
		}
	}*/
}

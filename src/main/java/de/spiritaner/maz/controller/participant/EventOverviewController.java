package de.spiritaner.maz.controller.participant;

import de.spiritaner.maz.controller.ControllerAnnotation;
import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.model.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.time.LocalDate;
import java.util.Collection;

@ControllerAnnotation(fxmlFile = "/fxml/participant/event_overview.fxml", objDesc = "Veranstaltung")
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
}

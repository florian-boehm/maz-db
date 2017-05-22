package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.dialog.RemoveDialog;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.meta.EventType;
import de.spiritaner.maz.util.document.ParticipantList;
import de.spiritaner.maz.util.factory.DateAsStringListCell;
import de.spiritaner.maz.util.factory.MetaClassTableCell;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.time.LocalDate;
import java.util.Collection;

@OverviewController.Annotation(fxmlFile = "/fxml/participation/event_overview.fxml", objDesc = "Veranstaltung")
public class EventOverviewController extends OverviewController<Event> {

	@FXML private TableColumn<Event, String> locationColumn;
	@FXML private TableColumn<Event, EventType> eventTypeColumn;
	@FXML private TableColumn<Event, String> nameColumn;
	@FXML private TableColumn<Event, LocalDate> fromDateColumn;
	@FXML private TableColumn<Event, LocalDate> toDateColumn;
	@FXML private TableColumn<Event, Long> idColumn;

	public EventOverviewController() {
		super(Event.class, true);
	}

	@Override
	protected void handleException(RollbackException e, Event event) {
		RemoveDialog.showFailureAndWait("Event","Event",e);
	}

	@Override
	protected String getLoadingText() {
		return "Lade Veranstaltungen ...";
	}

	@Override
	protected Collection<Event> preLoad(EntityManager em) {
		return em.createNamedQuery("Event.findAll", Event.class).getResultList();
	}

	@Override
	protected void postInit() {
		eventTypeColumn.setCellValueFactory(cellData -> cellData.getValue().eventTypeProperty());
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		locationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
		fromDateColumn.setCellValueFactory(cellData -> cellData.getValue().fromDateProperty());
		toDateColumn.setCellValueFactory(cellData -> cellData.getValue().toDateProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

		eventTypeColumn.setCellFactory(column -> new MetaClassTableCell<>());
		fromDateColumn.setCellFactory(column -> DateAsStringListCell.localDateTableCell());
		toDateColumn.setCellFactory(column -> DateAsStringListCell.localDateTableCell());
	}
}

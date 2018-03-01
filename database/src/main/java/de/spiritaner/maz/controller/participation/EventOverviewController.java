package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.meta.EventType;
import de.spiritaner.maz.view.dialog.RemoveDialog;
import de.spiritaner.maz.view.renderer.DateAsStringListCell;
import de.spiritaner.maz.view.renderer.MetaClassTableCell;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.time.LocalDate;
import java.util.Collection;

@OverviewController.Annotation(fxmlFile = "/fxml/participation/event_overview.fxml", objDesc = "$event")
public class EventOverviewController extends OverviewController<Event> {

	public TableColumn<Event, String> locationColumn;
	public TableColumn<Event, EventType> eventTypeColumn;
	public TableColumn<Event, String> nameColumn;
	public TableColumn<Event, LocalDate> fromDateColumn;
	public TableColumn<Event, LocalDate> toDateColumn;

	public EventOverviewController() {
		super(Event.class, true);
	}

	@Override
	protected void handleException(RollbackException e, Event event) {
		String objName = guiText.getString("event");
		RemoveDialog.showFailureAndWait(objName, objName, e);
	}

	@Override
	protected String getLoadingText() {
		return guiText.getString("loading") + " " + guiText.getString("events") + " ...";
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

		eventTypeColumn.setCellFactory(column -> new MetaClassTableCell<>());
		fromDateColumn.setCellFactory(column -> DateAsStringListCell.localDateTableCell());
		toDateColumn.setCellFactory(column -> DateAsStringListCell.localDateTableCell());
	}
}

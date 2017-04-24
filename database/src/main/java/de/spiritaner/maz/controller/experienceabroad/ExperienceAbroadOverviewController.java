package de.spiritaner.maz.controller.experienceabroad;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.dialog.RemoveDialog;
import de.spiritaner.maz.model.ContactMethod;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.ExperienceAbroad;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.meta.EventType;
import de.spiritaner.maz.util.factory.DateAsStringListCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.time.LocalDate;
import java.util.Collection;

public class ExperienceAbroadOverviewController extends OverviewController<ExperienceAbroad> {

	@FXML private TableColumn<ExperienceAbroad, String> communityColumn;
	@FXML private TableColumn<ExperienceAbroad, String> locationColumn;
	@FXML private TableColumn<ExperienceAbroad, LocalDate> fromDateColumn;
	@FXML private TableColumn<ExperienceAbroad, LocalDate> toDateColumn;
	@FXML private TableColumn<ExperienceAbroad, Long> idColumn;

	private Person person;

	public ExperienceAbroadOverviewController() {
		super(ExperienceAbroad.class, true);
	}

	@Override
	protected void preCreate(ExperienceAbroad newExperienceAbroad) {
		newExperienceAbroad.setPerson(person);
	}

	@Override
	protected void postRemove(ExperienceAbroad obsoleteEntity) {
		person.getExperiencesAbroad().remove(obsoleteEntity);
	}

	@Override
	protected Collection<ExperienceAbroad> preLoad(EntityManager em) {
		if(person != null) {
			Hibernate.initialize(person.getExperiencesAbroad());
			return FXCollections.observableArrayList(person.getExperiencesAbroad());
		} else {
			return FXCollections.emptyObservableList();
		}
	}

	@Override
	protected String getLoadingText() {
		return "Lade Mitlebezeiten ...";
	}

	@Override
	protected void handleException(RollbackException e, ExperienceAbroad selectedObj) {
		RemoveDialog.showFailureAndWait("Mitlebezeit","Mitlebezeit",e);
	}

	@Override
	protected void postInit() {
		communityColumn.setCellValueFactory(cellData -> cellData.getValue().communityProperty());
		locationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
		fromDateColumn.setCellValueFactory(cellData -> cellData.getValue().fromDateProperty());
		toDateColumn.setCellValueFactory(cellData -> cellData.getValue().toDateProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

		fromDateColumn.setCellFactory(column -> DateAsStringListCell.localDateTableCell());
		toDateColumn.setCellFactory(column -> DateAsStringListCell.localDateTableCell());
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}

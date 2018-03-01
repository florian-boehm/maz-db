package de.spiritaner.maz.controller.experienceabroad;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.model.ExperienceAbroad;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.view.dialog.RemoveDialog;
import de.spiritaner.maz.view.renderer.DateAsStringListCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.time.LocalDate;
import java.util.Collection;

public class ExperienceAbroadOverviewController extends OverviewController<ExperienceAbroad> {

	public TableColumn<ExperienceAbroad, String> communityColumn;
	public TableColumn<ExperienceAbroad, String> locationColumn;
	public TableColumn<ExperienceAbroad, LocalDate> fromDateColumn;
	public TableColumn<ExperienceAbroad, LocalDate> toDateColumn;

	public ObjectProperty<Person> person = new SimpleObjectProperty<>();

	public ExperienceAbroadOverviewController() {
		super(ExperienceAbroad.class, true);
	}

	@Override
	protected void preCreate(ExperienceAbroad newExperienceAbroad) {
		newExperienceAbroad.setPerson(person.get());
	}

	@Override
	protected void postRemove(ExperienceAbroad obsoleteEntity) {
		person.get().getExperiencesAbroad().remove(obsoleteEntity);
	}

	@Override
	protected Collection<ExperienceAbroad> preLoad(EntityManager em) {
		if(person != null) {
			Hibernate.initialize(person.get().getExperiencesAbroad());
			return FXCollections.observableArrayList(person.get().getExperiencesAbroad());
		} else {
			return FXCollections.emptyObservableList();
		}
	}

	@Override
	// TODO Extract strings
	protected String getLoadingText() {
		return "Lade Mitlebezeiten ...";
	}

	@Override
	// TODO Extract strings
	protected void handleException(RollbackException e, ExperienceAbroad selectedObj) {
		RemoveDialog.showFailureAndWait("Mitlebezeit","Mitlebezeit",e);
	}

	@Override
	protected void postInit() {
		communityColumn.setCellValueFactory(cellData -> cellData.getValue().communityProperty());
		locationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
		fromDateColumn.setCellValueFactory(cellData -> cellData.getValue().fromDateProperty());
		toDateColumn.setCellValueFactory(cellData -> cellData.getValue().toDateProperty());

		fromDateColumn.setCellFactory(column -> DateAsStringListCell.localDateTableCell());
		toDateColumn.setCellFactory(column -> DateAsStringListCell.localDateTableCell());
	}
}

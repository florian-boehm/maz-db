package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.dialog.RemoveDialog;
import de.spiritaner.maz.model.*;
import de.spiritaner.maz.util.factory.BooleanTableCell;
import de.spiritaner.maz.util.factory.DateAsStringListCell;
import de.spiritaner.maz.util.factory.EPNumberCell;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.time.LocalDate;
import java.util.Collection;

public class YearAbroadOverviewController extends OverviewController<YearAbroad> {

	@FXML
	private TableColumn<YearAbroad, String> personColumn;
	@FXML
	private TableColumn<YearAbroad, String> siteColumn;
	@FXML
	private TableColumn<YearAbroad, String> jobDescriptionColumn;
	@FXML
	private TableColumn<YearAbroad, LocalDate> departureDateColumn;
	@FXML
	private TableColumn<YearAbroad, LocalDate> arrivalDateColumn;
	@FXML
	private TableColumn<YearAbroad, LocalDate> abortionDateColumn;
	@FXML
	private TableColumn<YearAbroad, Boolean> weltwaertsPromotedColumn;
	@FXML
	private TableColumn<YearAbroad, EPNumber> epNumberColumn;
	@FXML
	private TableColumn<YearAbroad, Long> idColumn;

	private Site site;
	private Person person;

	public YearAbroadOverviewController() {
		super(YearAbroad.class, true);
	}

	@Override
	protected Collection<YearAbroad> preLoad(EntityManager em) {
		if(site != null) {
			Hibernate.initialize(site.getYearsAbroad());
			return site.getYearsAbroad();
		} else  if(person != null) {
			Hibernate.initialize(person.getYearsAbroad());
			return person.getYearsAbroad();
		} else {
			return null;
		}
	}

	@Override
	protected String getLoadingText() {
		return "Lade Auslandsjahre ...";
	}

	@Override
	protected void handleException(RollbackException e, YearAbroad yearAbroad) {
		// TODO choose better text here
		RemoveDialog.showFailureAndWait("Auslandsjahr","Auslandsjahr", e);
	}

	@Override
	protected void postInit() {
		personColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPerson().getFullName()));
		siteColumn.setCellValueFactory(cellData -> cellData.getValue().getSite().nameProperty());
		departureDateColumn.setCellValueFactory(cellData -> cellData.getValue().departureDateProperty());
		arrivalDateColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalDateProperty());
		abortionDateColumn.setCellValueFactory(cellData -> cellData.getValue().abortionDateProperty());
		weltwaertsPromotedColumn.setCellValueFactory(cellData -> cellData.getValue().weltwaertsPromotedProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
		jobDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().jobDescriptionProperty());
		epNumberColumn.setCellValueFactory(cellData -> cellData.getValue().epNumberProperty());

		epNumberColumn.setCellFactory(column -> EPNumberCell.epNumberTableCell());
		weltwaertsPromotedColumn.setCellFactory(column -> new BooleanTableCell<>());
		departureDateColumn.setCellFactory(column -> DateAsStringListCell.localDateTableCell());
		arrivalDateColumn.setCellFactory(column -> DateAsStringListCell.localDateTableCell());
		abortionDateColumn.setCellFactory(column -> DateAsStringListCell.localDateTableCell());
	}

	@Override
	protected void preCreate(YearAbroad yearAbroad) {
		if(site != null) yearAbroad.setSite(site);
		if(person != null) yearAbroad.setPerson(person);
	}

	public void setSite(Site site) {
		this.site = site;

		if(siteColumn != null) siteColumn.setVisible(false);
	}

	public void setPerson(Person person) {
		this.person = person;

		if(personColumn != null) personColumn.setVisible(false);
	}
}
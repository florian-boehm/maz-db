package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Responsible;
import de.spiritaner.maz.model.Site;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

public class ResponsibleOverviewController extends OverviewController<Responsible> {

	@FXML
	private TableColumn<Responsible, String> jobDescriptionColumn;
	@FXML
	private TableColumn<Responsible, String> homeCountryColumn;
	@FXML
	private TableColumn<Responsible, String> personColumn;
	@FXML
	private TableColumn<Responsible, String> siteColumn;
	@FXML
	private TableColumn<Responsible, String> groupColumn;
	@FXML
	private TableColumn<Responsible, Long> idColumn;

	private Site site;
	private Person person;

	public ResponsibleOverviewController() {
		super(Responsible.class, true);
	}

	@Override
	protected Collection<Responsible> preLoad(EntityManager em) {
		if(site != null) {
			Hibernate.initialize(site.getResponsibles());
			return site.getResponsibles();
		} else if(person != null) {
			Hibernate.initialize(person.getResponsibles());
			return person.getResponsibles();
		} else {
			return null;
		}
	}

	@Override
	protected String getLoadingText() {
		return "Lade Verantwortliche ...";
	}

	@Override
	protected void handleException(RollbackException e) {
		ExceptionDialog.show(e);
	}

	@Override
	protected void postInit() {
		jobDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().jobDescriptionProperty());
		homeCountryColumn.setCellValueFactory(cellData -> cellData.getValue().homeCountryProperty());
		personColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPerson().getFullName()));
		siteColumn.setCellValueFactory(cellData -> cellData.getValue().getSite().nameProperty());
		groupColumn.setCellValueFactory(cellData -> cellData.getValue().groupNameProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
	}

	public void setSite(Site site) {
		this.site = site;

		if(siteColumn != null) siteColumn.setVisible(false);
	}

	@Override
	protected void preCreate(Responsible responsible) {
		responsible.setSite(site);
	}

	public void setPerson(Person person) {
		this.person = person;

		if(personColumn != null) personColumn.setVisible(false);
	}
}

package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Responsible;
import de.spiritaner.maz.model.Site;
import de.spiritaner.maz.model.meta.PersonGroup;
import de.spiritaner.maz.view.dialog.RemoveDialog;
import de.spiritaner.maz.view.renderer.MetaClassTableCell;
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
	private TableColumn<Responsible, PersonGroup> personGroupColumn;

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
	protected void handleException(RollbackException e, Responsible responsible) {
		// TODO choose better text here
		RemoveDialog.showFailureAndWait("Verantwortliche(r)","Verantwortliche(r)", e);
	}

	@Override
	protected void postInit() {
		jobDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().jobDescriptionProperty());
		homeCountryColumn.setCellValueFactory(cellData -> cellData.getValue().homeCountryProperty());
		personColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPerson().getFullName()));
		siteColumn.setCellValueFactory(cellData -> cellData.getValue().getSite().nameProperty());
		personGroupColumn.setCellValueFactory(cellData -> cellData.getValue().personGroupProperty());

		personGroupColumn.setCellFactory(column -> new MetaClassTableCell<>());
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

package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Responsible;
import de.spiritaner.maz.model.Site;
import de.spiritaner.maz.model.meta.PersonGroup;
import de.spiritaner.maz.view.dialog.RemoveDialog;
import de.spiritaner.maz.view.renderer.MetaClassTableCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

public class ResponsibleOverviewController extends OverviewController<Responsible> {

	public TableColumn<Responsible, String> jobDescriptionColumn;
	public TableColumn<Responsible, String> homeCountryColumn;
	public TableColumn<Responsible, String> personColumn;
	public TableColumn<Responsible, String> siteColumn;
	public TableColumn<Responsible, PersonGroup> personGroupColumn;

	public ObjectProperty<Site> site = new SimpleObjectProperty<>();
	public ObjectProperty<Person> person = new SimpleObjectProperty<>();

	public ResponsibleOverviewController() {
		super(Responsible.class, true);
	}

	@Override
	protected Collection<Responsible> preLoad(EntityManager em) {
		if(site.get() != null) {
			Hibernate.initialize(site.get().getResponsibles());
			return site.get().getResponsibles();
		} else if(person.get() != null) {
			Hibernate.initialize(person.get().getResponsibles());
			return person.get().getResponsibles();
		} else {
			return null;
		}
	}

	@Override
	protected String getLoadingText() {
		return guiText.getString("loading") + " " + guiText.getString("responsibles") + " ...";
	}

	@Override
	protected void handleException(RollbackException e, Responsible responsible) {
		// TODO choose better text her
		String objName = guiText.getString("responsible");
		RemoveDialog.showFailureAndWait(objName, objName, e);
	}

	@Override
	protected void postInit() {
		jobDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().jobDescriptionProperty());
		homeCountryColumn.setCellValueFactory(cellData -> cellData.getValue().homeCountryProperty());
		personColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPerson().getFullName()));
		siteColumn.setCellValueFactory(cellData -> cellData.getValue().getSite().nameProperty());
		personGroupColumn.setCellValueFactory(cellData -> cellData.getValue().personGroupProperty());

		personGroupColumn.setCellFactory(column -> new MetaClassTableCell<>());

		siteColumn.visibleProperty().bind(site.isNull());
		personColumn.visibleProperty().bind(person.isNull());
	}

	@Override
	protected void preCreate(Responsible responsible) {
		responsible.site.bindBidirectional(site);
	}
}

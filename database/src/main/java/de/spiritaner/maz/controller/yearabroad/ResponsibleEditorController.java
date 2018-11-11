package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.meta.PersonGroupOverviewController;
import de.spiritaner.maz.model.Responsible;
import de.spiritaner.maz.model.Site;
import de.spiritaner.maz.model.meta.PersonGroup;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.Selected;
import de.spiritaner.maz.view.component.BindableComboBox;
import de.spiritaner.maz.view.component.SimpleBindableComboBox;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class ResponsibleEditorController extends EditorController {

	final static Logger logger = Logger.getLogger(ResponsibleEditorController.class);

	public ObjectProperty<Site> site = new SimpleObjectProperty<>();
	public ObjectProperty<Responsible> responsible = new SimpleObjectProperty<>();

	public SimpleBindableComboBox<String> jobDescriptionComboBox;
	public BindableComboBox<PersonGroup> personGroupComboBox;
	public SimpleBindableComboBox<String> homeCountryComboBox;

	public void initialize(URL location, ResourceBundle resources) {
		autoBinder.register(this);
		site.addListener((observable, oldValue, newValue) -> {
			autoBinder.rebindAll();
			loadHomeCountries();
			loadPersonGroups();
			loadJobDescriptions();
		});
		responsible.addListener((observable, oldValue, newValue) -> autoBinder.rebindAll());

		loadPersonGroups();
		loadJobDescriptions();
		loadHomeCountries();

		autoValidator.add(personGroupComboBox, new Selected(personGroupComboBox, guiText.getString("person_group")));
	}

	private void loadPersonGroups() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<PersonGroup> results = em.createNamedQuery("PersonGroup.findAll", PersonGroup.class).getResultList();

		personGroupComboBox.populate(results, null);
	}

	private void loadHomeCountries() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();

		TypedQuery<String> query;
		if(site != null) {
			query = em.createNamedQuery("Responsible.findHomeCountriesDistinctForSite", String.class);
			query.setParameter("site", site);
		} else {
			query = em.createNamedQuery("Responsible.findHomeCountriesDistinct", String.class);
		}

		homeCountryComboBox.getItems().clear();
		homeCountryComboBox.getItems().addAll(FXCollections.observableArrayList(query.getResultList()));
	}

	private void loadJobDescriptions() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();

		TypedQuery<String> query;
		if(site != null) {
			query = em.createNamedQuery("Responsible.findJobDescriptionsDistinctForSite", String.class);
			query.setParameter("site", site);
		} else {
			query = em.createNamedQuery("Responsible.findJobDescriptionsDistinct", String.class);
		}

		jobDescriptionComboBox.getItems().clear();
		jobDescriptionComboBox.getItems().addAll(FXCollections.observableArrayList(query.getResultList()));
	}

	public void addNewPersonGroup(ActionEvent actionEvent) {
		new PersonGroupOverviewController().create(actionEvent);

		loadPersonGroups();
	}
}

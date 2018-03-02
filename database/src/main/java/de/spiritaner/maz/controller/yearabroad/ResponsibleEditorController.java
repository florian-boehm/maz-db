package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.meta.PersonGroupOverviewController;
import de.spiritaner.maz.model.Responsible;
import de.spiritaner.maz.model.Site;
import de.spiritaner.maz.model.meta.PersonGroup;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.ComboBoxValidator;
import de.spiritaner.maz.view.binding.AutoBinder;
import de.spiritaner.maz.view.component.BindableComboBox;
import de.spiritaner.maz.view.component.SimpleBindableComboBox;
import de.spiritaner.maz.view.renderer.MetaClassListCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
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

	private ComboBoxValidator<PersonGroup> personGroupValidator;

	public void initialize(URL location, ResourceBundle resources) {
		AutoBinder ab = new AutoBinder(this);
		site.addListener((observable, oldValue, newValue) -> {
			ab.rebindAll();
			loadHomeCountries();
			loadPersonGroups();
			loadJobDescriptions();
		});
		responsible.addListener((observable, oldValue, newValue) -> ab.rebindAll());

		loadPersonGroups();
		loadJobDescriptions();
		loadHomeCountries();

		personGroupValidator = new ComboBoxValidator<>(personGroupComboBox).fieldName("Personengruppe").isSelected(true).validateOnChange();
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

	public boolean isValid() {
		return personGroupValidator.validate();
	}

	public void addNewPersonGroup(ActionEvent actionEvent) {
		new PersonGroupOverviewController().create(actionEvent);

		loadPersonGroups();
	}
}

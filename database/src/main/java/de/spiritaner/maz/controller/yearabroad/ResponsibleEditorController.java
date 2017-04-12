package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.meta.PersonGroupOverviewController;
import de.spiritaner.maz.model.Responsible;
import de.spiritaner.maz.model.Site;
import de.spiritaner.maz.model.meta.PersonGroup;
import de.spiritaner.maz.util.DataDatabase;
import de.spiritaner.maz.util.factory.MetaClassListCell;
import de.spiritaner.maz.util.validator.ComboBoxValidator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.net.URL;
import java.util.ResourceBundle;

public class ResponsibleEditorController implements Initializable {

	final static Logger logger = Logger.getLogger(ResponsibleEditorController.class);

	@FXML
	private ComboBox<String> jobDescriptionComboBox;
	@FXML
	private ComboBox<PersonGroup> personGroupComboBox;
	@FXML
	private ComboBox<String> homeCountryComboBox;

	private Site site;
	private ComboBoxValidator<PersonGroup> personGroupValidator;

	public void initialize(URL location, ResourceBundle resources) {
		loadPersonGroups();
		loadJobDescriptions();
		loadHomeCountries();

		personGroupValidator = new ComboBoxValidator<>(personGroupComboBox).fieldName("Personengruppe").isSelected(true).validateOnChange();
		personGroupComboBox.setCellFactory((column) -> new MetaClassListCell<>());
		personGroupComboBox.setButtonCell(new MetaClassListCell<>());
	}

	public void setAll(Responsible responsible) {
		jobDescriptionComboBox.setValue(responsible.getJobDescription());
		personGroupComboBox.setValue(responsible.getPersonGroup());
		homeCountryComboBox.setValue(responsible.getHomeCountry());
	}

	public Responsible getAll(Responsible responsible) {
		if (responsible == null) responsible = new Responsible();
		responsible.setJobDescription(jobDescriptionComboBox.getValue());
		responsible.setHomeCountry(homeCountryComboBox.getValue());
		responsible.setPersonGroup(personGroupComboBox.getValue());
		return responsible;
	}

	public void setReadonly(boolean readonly) {
		jobDescriptionComboBox.setDisable(readonly);
		personGroupComboBox.setDisable(readonly);
		homeCountryComboBox.setDisable(readonly);
	}

	private void loadPersonGroups() {
		EntityManager em = DataDatabase.getFactory().createEntityManager();

		TypedQuery<PersonGroup> query = em.createNamedQuery("PersonGroup.findAll", PersonGroup.class);

		PersonGroup selectedBefore = personGroupComboBox.getSelectionModel().getSelectedItem();
		personGroupComboBox.getItems().clear();
		personGroupComboBox.getItems().addAll(FXCollections.observableArrayList(query.getResultList()));
		personGroupComboBox.getSelectionModel().select(selectedBefore);
	}

	private void loadHomeCountries() {
		EntityManager em = DataDatabase.getFactory().createEntityManager();

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
		EntityManager em = DataDatabase.getFactory().createEntityManager();

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

	public void setSite(Site site) {
		this.site = site;

		loadPersonGroups();
		loadHomeCountries();
		loadJobDescriptions();
	}

	public void addNewPersonGroup(ActionEvent actionEvent) {
		new PersonGroupOverviewController().create(actionEvent);

		loadPersonGroups();
	}
}

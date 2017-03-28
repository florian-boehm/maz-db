package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.model.Responsible;
import de.spiritaner.maz.model.Site;
import de.spiritaner.maz.util.DataDatabase;
import javafx.collections.FXCollections;
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
	private ComboBox<String> groupNameComboBox;
	@FXML
	private ComboBox<String> homeCountryComboBox;

	private Site site;

	public void initialize(URL location, ResourceBundle resources) {
		loadGroupNames();
		loadJobDescriptions();
		loadHomeCountries();
	}

	public void setAll(Responsible responsible) {
		jobDescriptionComboBox.setValue(responsible.getJobDescription());
		groupNameComboBox.setValue(responsible.getGroupName());
		homeCountryComboBox.setValue(responsible.getHomeCountry());
	}

	public Responsible getAll(Responsible responsible) {
		if (responsible == null) responsible = new Responsible();
		responsible.setJobDescription(jobDescriptionComboBox.getValue());
		responsible.setHomeCountry(homeCountryComboBox.getValue());
		responsible.setGroupName(groupNameComboBox.getValue());
		return responsible;
	}

	public void setReadonly(boolean readonly) {
		jobDescriptionComboBox.setDisable(readonly);
		groupNameComboBox.setDisable(readonly);
		homeCountryComboBox.setDisable(readonly);
	}

	private void loadGroupNames() {
		EntityManager em = DataDatabase.getFactory().createEntityManager();

		TypedQuery<String> query;
		if(site != null) {
			query = em.createNamedQuery("Responsible.findGroupNamesDistinctForSite", String.class);
			query.setParameter("site", site);
		} else {
			query = em.createNamedQuery("Responsible.findGroupNamesDistinct", String.class);
		}

		groupNameComboBox.getItems().clear();
		groupNameComboBox.getItems().addAll(FXCollections.observableArrayList(query.getResultList()));
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
		return true;
	}

	public void setSite(Site site) {
		this.site = site;

		loadGroupNames();
		loadHomeCountries();
		loadJobDescriptions();
	}
}

package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.controller.person.PersonOverviewController;
import de.spiritaner.maz.model.EPNumber;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Site;
import de.spiritaner.maz.model.YearAbroad;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.view.dialog.EditorDialog;
import de.spiritaner.maz.view.dialog.OverviewDialog;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/yearabroad/yearabroad_editor_dialog.fxml", objDesc = "$year_abroad")
public class YearAbroadEditorDialogController extends EditorDialogController<YearAbroad> {

	final static Logger logger = Logger.getLogger(YearAbroadEditorDialogController.class);

	public GridPane siteEditor;
	public SiteEditorController siteEditorController;
	public GridPane yearAbroadEditor;
	public YearAbroadEditorController yearAbroadEditorController;
	public GridPane personEditor;
	public PersonEditorController personEditorController;
	public Button searchPersonButton;
	public Button searchSiteButton;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		yearAbroadEditorController.departureDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> searchEPNumber());

		/*yearAbroadEditorController.getArrivalDatePicker().valueProperty().addListener((observable, oldValue, newValue) -> {
			searchEPNumber();
		});*/
	}

	private void searchEPNumber() {
		EPNumber previousEpNumber = yearAbroadEditorController.epNumberComboBox.getValue();
		yearAbroadEditorController.epNumberComboBox.getItems().clear();

		if(/*yearAbroadEditorController.getArrivalDatePicker().getValue() != null &&*/
				  yearAbroadEditorController.departureDatePicker.getValue() != null &&
				  getIdentifiable() != null && getIdentifiable().getSite() != null) {
			EntityManager em = CoreDatabase.getFactory().createEntityManager();
			em.getTransaction().begin();

			TypedQuery<YearAbroad> query = em.createNamedQuery("YearAbroad.findAllOfSiteInYear", YearAbroad.class);
			query.setParameter("site",getIdentifiable().getSite());
			query.setParameter("year", yearAbroadEditorController.departureDatePicker.getValue().getYear());
			/*query.setParameter("arrivalDate", yearAbroadEditorController.getArrivalDatePicker().getValue());*/

			final List<EPNumber> availableEpNumbers = new ArrayList<>();
			getIdentifiable().getSite().getEpNumbers().forEach(epNumber -> availableEpNumbers.add(epNumber));
			//getIdentifiable().getSite().getEpNumbers().forEach(epNumber -> logger.info("all: "+epNumber));

			query.getResultList().forEach(yearAbroad -> {
				logger.info("ep number may be in use: "+yearAbroad.getEpNumber());
				logger.info("year abroad ids: " + yearAbroad.getId() + " = " + getIdentifiable().getId());
				if(!yearAbroad.equals(getIdentifiable()) && availableEpNumbers.contains(yearAbroad.getEpNumber())) {
					logger.info("remove: "+yearAbroad.getEpNumber());
					availableEpNumbers.remove(yearAbroad.getEpNumber());
				}
			});

			//availableEpNumbers.forEach(epNumber -> logger.info("available: "+epNumber));

			yearAbroadEditorController.epNumberComboBox.getItems().addAll(availableEpNumbers);
			em.getTransaction().commit();
		} else if(getIdentifiable() != null && getIdentifiable().getSite() != null) {
			//getIdentifiable().getSite().getEpNumbers().forEach(epNumber -> logger.info("all: "+epNumber));
			yearAbroadEditorController.epNumberComboBox.getItems().addAll(getIdentifiable().getSite().getEpNumbers());
		}

		if(previousEpNumber != null && yearAbroadEditorController.epNumberComboBox.getItems().contains(previousEpNumber)) {
			yearAbroadEditorController.epNumberComboBox.getSelectionModel().select(previousEpNumber);
		}
	}

	@Override
	protected void bind(YearAbroad yearAbroad) {
		siteEditorController.site.bindBidirectional(yearAbroad.site);
		siteEditorController.readOnly.set(true);

		personEditorController.person.bindBidirectional(yearAbroad.person);
		personEditorController.readOnly.set(true);

		searchSiteButton.disableProperty().bind(yearAbroad.site.isNotNull());
		searchPersonButton.disableProperty().bind(yearAbroad.person.isNotNull());

		if(yearAbroad.getSite() != null) searchEPNumber();

		yearAbroadEditorController.yearAbroad.bindBidirectional(identifiable);
	}

	@Override
	protected boolean allValid() {
		boolean siteValid = siteEditorController.isValid();
		boolean personValid = personEditorController.isValid();
		boolean yearAbroadValid = yearAbroadEditorController.isValid();

		return siteValid && personValid && yearAbroadValid;
	}

	public void searchPerson(ActionEvent actionEvent) {
		OverviewDialog<PersonOverviewController, Person> dialog = new OverviewDialog<>(PersonOverviewController.class);
		Optional<Person> result = dialog.showAndSelect(getStage());

		result.ifPresent((Person person) -> {
			identifiable.get().person.set(person);
		});
	}

	public void searchSite(ActionEvent actionEvent) {
		OverviewDialog<SiteOverviewController, Site> dialog = new OverviewDialog<>(SiteOverviewController.class);
		Optional<Site> result = dialog.showAndSelect(getStage());

		result.ifPresent((Site site) -> {
			identifiable.get().site.set(site);
			searchEPNumber();
		});
	}
}

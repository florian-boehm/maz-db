package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.controller.person.PersonOverviewController;
import de.spiritaner.maz.view.dialog.EditorDialog;
import de.spiritaner.maz.view.dialog.OverviewDialog;
import de.spiritaner.maz.model.EPNumber;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Site;
import de.spiritaner.maz.model.YearAbroad;
import de.spiritaner.maz.util.database.CoreDatabase;
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

@EditorDialog.Annotation(fxmlFile = "/fxml/yearabroad/yearabroad_editor_dialog.fxml", objDesc = "Auslandsjahr")
public class YearAbroadEditorDialogController extends EditorController<YearAbroad> {

	final static Logger logger = Logger.getLogger(YearAbroadEditorDialogController.class);

	@FXML private Text titleText;
	@FXML private GridPane siteEditor;
	@FXML private SiteEditorController siteEditorController;
	@FXML private GridPane yearAbroadEditor;
	@FXML private YearAbroadEditorController yearAbroadEditorController;
	@FXML private GridPane personEditor;
	@FXML private PersonEditorController personEditorController;
	@FXML private Button saveYearAbroadButton;
	@FXML private Button searchPersonButton;
	@FXML private Button searchSiteButton;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		yearAbroadEditorController.getDepartureDatePicker().valueProperty().addListener((observable, oldValue, newValue) -> searchEPNumber());

		/*yearAbroadEditorController.getArrivalDatePicker().valueProperty().addListener((observable, oldValue, newValue) -> {
			searchEPNumber();
		});*/
	}

	private void searchEPNumber() {
		EPNumber previousEpNumber = yearAbroadEditorController.getEpNumberComboBox().getValue();
		yearAbroadEditorController.getEpNumberComboBox().getItems().clear();

		if(/*yearAbroadEditorController.getArrivalDatePicker().getValue() != null &&*/
				  yearAbroadEditorController.getDepartureDatePicker().getValue() != null &&
				  getIdentifiable() != null && getIdentifiable().getSite() != null) {
			EntityManager em = CoreDatabase.getFactory().createEntityManager();
			em.getTransaction().begin();

			TypedQuery<YearAbroad> query = em.createNamedQuery("YearAbroad.findAllOfSiteInYear", YearAbroad.class);
			query.setParameter("site",getIdentifiable().getSite());
			query.setParameter("year", yearAbroadEditorController.getDepartureDatePicker().getValue().getYear());
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

			yearAbroadEditorController.getEpNumberComboBox().getItems().addAll(availableEpNumbers);
			em.getTransaction().commit();
		} else if(getIdentifiable() != null && getIdentifiable().getSite() != null) {
			//getIdentifiable().getSite().getEpNumbers().forEach(epNumber -> logger.info("all: "+epNumber));
			yearAbroadEditorController.getEpNumberComboBox().getItems().addAll(getIdentifiable().getSite().getEpNumbers());
		}

		if(previousEpNumber != null && yearAbroadEditorController.getEpNumberComboBox().getItems().contains(previousEpNumber)) {
			yearAbroadEditorController.getEpNumberComboBox().getSelectionModel().select(previousEpNumber);
		}
	}

	@Override
	public void setIdentifiable(YearAbroad yearAbroad) {
		super.setIdentifiable(yearAbroad);

		siteEditorController.setReadonly(true);
		personEditorController.setReadonly(true);

		if (yearAbroad != null) {
			if (yearAbroad.getSite() != null) {
				siteEditorController.setAll(yearAbroad.getSite());
				searchSiteButton.setDisable(true);

				searchEPNumber();
			}

			if (yearAbroad.getPerson() != null) {
				personEditorController.setAll(yearAbroad.getPerson());
				searchPersonButton.setDisable(true);
			}

			yearAbroadEditorController.setAll(yearAbroad);

			if (yearAbroad.getId() != 0L) {
				titleText.setText("Auslandsjahr bearbeiten");
				saveYearAbroadButton.setText("Speichern");
			} else {
				titleText.setText("Auslandsjahr anlegen");
				saveYearAbroadButton.setText("Anlegen");
			}
		}
	}

	public void saveYearAbroad(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			boolean siteValid = siteEditorController.isValid();
			boolean personValid = personEditorController.isValid();
			boolean yearAbroadValid = yearAbroadEditorController.isValid();

			if(siteValid && personValid && yearAbroadValid) {
				EntityManager em = CoreDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				getIdentifiable().setPerson(personEditorController.getAll(getIdentifiable().getPerson()));
				getIdentifiable().setSite(siteEditorController.getAll(getIdentifiable().getSite()));
				yearAbroadEditorController.getAll(getIdentifiable());

				try {
					// TODO this is the best way to do it, so it should be copied over to other editor dialog controllers
					YearAbroad managedYearAbroad = (!em.contains(getIdentifiable())) ? em.merge(getIdentifiable()) : getIdentifiable();
					em.getTransaction().commit();
					setResult(managedYearAbroad);
					requestClose();
				} catch (PersistenceException e) {
					em.getTransaction().rollback();
					logger.warn(e);
				} finally {
					em.close();
				}
			}
		});
	}

	public void searchPerson(ActionEvent actionEvent) {
		OverviewDialog<PersonOverviewController, Person> dialog = new OverviewDialog<>(PersonOverviewController.class);
		Optional<Person> result = dialog.showAndWait(getStage());

		result.ifPresent((Person person) -> {
			getIdentifiable().setPerson(person);
			personEditorController.setAll(person);
		});
	}

	public void searchSite(ActionEvent actionEvent) {
		OverviewDialog<SiteOverviewController, Site> dialog = new OverviewDialog<>(SiteOverviewController.class);
		Optional<Site> result = dialog.showAndWait(getStage());

		result.ifPresent((Site site) -> {
			getIdentifiable().setSite(site);
			siteEditorController.setAll(site);
			searchEPNumber();
		});
	}
}

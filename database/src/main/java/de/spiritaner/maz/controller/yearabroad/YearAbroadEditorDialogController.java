package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.controller.person.PersonOverviewController;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.dialog.OverviewDialog;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Site;
import de.spiritaner.maz.model.YearAbroad;
import de.spiritaner.maz.util.DataDatabase;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.Optional;

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
	public void setIdentifiable(YearAbroad yearAbroad) {
		super.setIdentifiable(yearAbroad);

		siteEditorController.setReadonly(true);
		personEditorController.setReadonly(true);

		if (yearAbroad != null) {
			if (yearAbroad.getSite() != null) {
				siteEditorController.setAll(yearAbroad.getSite());
				searchSiteButton.setDisable(true);
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

			if(siteValid && personValid) {
				EntityManager em = DataDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				getIdentifiable().setPerson(personEditorController.getAll(getIdentifiable().getPerson()));
				getIdentifiable().setSite(siteEditorController.getAll(getIdentifiable().getSite()));
				yearAbroadEditorController.getAll(getIdentifiable());

				try {
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
		});
	}
}

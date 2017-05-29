package de.spiritaner.maz.controller.experienceabroad;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.view.dialog.EditorDialog;
import de.spiritaner.maz.model.ExperienceAbroad;
import de.spiritaner.maz.util.database.DataDatabase;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/experienceabroad/experienceabroad_editor_dialog.fxml", objDesc = "Mitlebezeit")
public class ExperienceAbroadEditorDialogController extends EditorController<ExperienceAbroad> {

	final static Logger logger = Logger.getLogger(ExperienceAbroadEditorDialogController.class);

	@FXML
	private Button saveExperienceAbroadButton;
	@FXML
	private Text titleText;
	@FXML
	private GridPane personEditor;
	@FXML
	private PersonEditorController personEditorController;
	@FXML
	private GridPane experienceAbroadEditor;
	@FXML
	private ExperienceAbroadEditorController experienceAbroadEditorController;

	@Override
	public void setIdentifiable(ExperienceAbroad experienceAbroad) {
		super.setIdentifiable(experienceAbroad);

		if (experienceAbroad != null) {
			// Check if a person is already set in this residence
			if (experienceAbroad.getPerson() != null) {
				personEditorController.setAll(experienceAbroad.getPerson());
				personEditorController.setReadonly(true);
			}

			experienceAbroadEditorController.setAll(experienceAbroad);

			if (experienceAbroad.getId() != 0L) {
				titleText.setText(getIdentifiableName() + " bearbeiten");
				saveExperienceAbroadButton.setText("Speichern");
			} else {
				titleText.setText(getIdentifiableName() + " anlegen");
				saveExperienceAbroadButton.setText("Anlegen");
			}
		}
	}

	@Override
	public void onReopen() {
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
	}

	public void saveExperienceAbroad(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			boolean personValid = personEditorController.isValid();
			boolean experienceAbroadValid = experienceAbroadEditorController.isValid();

			if (personValid && experienceAbroadValid) {
				EntityManager em = DataDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				getIdentifiable().setPerson(personEditorController.getAll(getIdentifiable().getPerson()));
				experienceAbroadEditorController.getAll(getIdentifiable());

				try {
					ExperienceAbroad managedExperienceAbroad = (!em.contains(getIdentifiable())) ? em.merge(getIdentifiable()) : getIdentifiable();
					em.getTransaction().commit();
					setResult(managedExperienceAbroad);
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

	public void closeDialog(ActionEvent actionEvent) {
		Platform.runLater(() -> getStage().close());
	}
}

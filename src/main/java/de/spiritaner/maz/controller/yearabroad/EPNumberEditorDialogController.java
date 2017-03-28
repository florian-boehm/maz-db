package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.model.EPNumber;
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
import java.net.URL;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/yearabroad/epnumber_editor_dialog.fxml", objDesc = "EP-Nummer")
public class EPNumberEditorDialogController extends EditorController<EPNumber> {

	final static Logger logger = Logger.getLogger(EPNumberEditorDialogController.class);

	@FXML
	private GridPane epNumberEditor;
	@FXML
	private EPNumberEditorController epNumberEditorController;
	@FXML
	private Button saveEPNumberButton;
	@FXML
	private Text titleText;

	public void saveEPNumber(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			boolean epNumberValid = epNumberEditorController.isValid();

			if(epNumberValid) {
				EntityManager em = DataDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				epNumberEditorController.getAll(getIdentifiable());

				try {
					if(!em.contains(getIdentifiable())) em.merge(getIdentifiable());
					em.getTransaction().commit();

					getStage().close();
				} catch (PersistenceException e) {
					em.getTransaction().rollback();
					logger.warn(e);
				} finally {
					em.close();
				}
			}
		});
	}

	@Override
	public void onReopen() {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void closeDialog(ActionEvent actionEvent) {
		Platform.runLater(() -> getStage().close());
	}

	@Override
	public void setIdentifiable(EPNumber epNumber) {
		super.setIdentifiable(epNumber);

		if(epNumber != null) {
			epNumberEditorController.setAll(epNumber);

			if (epNumber.getId() != 0L) {
				titleText.setText("EP-Nummer bearbeiten");
				saveEPNumberButton.setText("Speichern");
			} else {
				titleText.setText("EP-Nummer anlegen");
				saveEPNumberButton.setText("Anlegen");
			}
		}
	}
}

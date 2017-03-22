package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.model.Residence;
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

@EditorDialog.Annotation(fxmlFile = "/fxml/residence/residence_editor_dialog.fxml", objDesc = "Wohnort")
public class ResidenceEditorDialogController extends EditorController<Residence> {

	final static Logger logger = Logger.getLogger(ResidenceEditorDialogController.class);

	@FXML
	private Button saveResidenceButton;
	@FXML
	private Text titleText;
	@FXML
	private GridPane personEditor;
	@FXML
	private PersonEditorController personEditorController;
	@FXML
	private GridPane addressEditor;
	@FXML
	private AddressEditorController addressEditorController;
	@FXML
	private GridPane residenceEditor;
	@FXML
	private ResidenceEditorController residenceEditorController;

	private Residence residence;

	@Override
	public void setIdentifiable(Residence obj) {
		setResidence(obj);
	}

	@Override
	public void onReopen() {
	}

	public void setResidence(Residence residence) {
		this.residence = residence;

		if (residence != null) {
			// Check if a person is already set in this residence
			if (residence.getPerson() != null) {
				personEditorController.setAll(residence.getPerson());
				personEditorController.setReadonly(true);
			}

			if (residence.getAddress() != null) {
				addressEditorController.setAll(residence.getAddress());
			}

			residenceEditorController.setAll(residence);

			if (residence.getId() != 0L) {
				titleText.setText("Wohnort bearbeiten");
				saveResidenceButton.setText("Speichern");
			} else {
				titleText.setText("Wohnort anlegen");
				saveResidenceButton.setText("Anlegen");
			}
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
	}

	public void saveResidence(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			boolean addressValid = addressEditorController.isValid();
			boolean personValid = personEditorController.isValid();
			boolean residenceValid = residenceEditorController.isValid();

			if (addressValid && personValid && residenceValid) {
				EntityManager em = DataDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				residence.setPerson(personEditorController.getAll(residence.getPerson()));
				residence.setAddress(Address.findSame(em, addressEditorController.getAll(residence.getAddress())));
				residenceEditorController.getAll(residence);

				try {
					if (!em.contains(residence)) em.merge(residence);

					// Add backwards relationship too
					if (!residence.getPerson().getResidences().contains(residence))
						residence.getPerson().getResidences().add(residence);

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

	public void closeDialog(ActionEvent actionEvent) {
		Platform.runLater(() -> getStage().close());
	}
}

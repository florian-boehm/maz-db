package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.view.dialog.EditorDialog;
import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.model.Residence;
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

	@Override
	public void setIdentifiable(Residence residence) {
		super.setIdentifiable(residence);

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

	public void saveResidence(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			boolean addressValid = addressEditorController.isValid();
			boolean personValid = personEditorController.isValid();
			boolean residenceValid = residenceEditorController.isValid();

			if (addressValid && personValid && residenceValid) {
				EntityManager em = CoreDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				getIdentifiable().setPerson(personEditorController.getAll(getIdentifiable().getPerson()));
				getIdentifiable().setAddress(Address.findSame(em, addressEditorController.getAll(getIdentifiable().getAddress())));
				residenceEditorController.getAll(getIdentifiable());

				try {
					Residence managedResidence = (!em.contains(getIdentifiable())) ? em.merge(getIdentifiable()) : getIdentifiable();

					// Add backwards relationship too
					// if (!managedResidence.getPerson().getResidences().contains(managedResidence)) managedResidence.getPerson().getResidences().add(managedResidence);
					//if(getIdentifiable().getPerson().setResidences())

					if (residenceEditorController.getPreferredResidence().isSelected()) {
						managedResidence.getPerson().setPreferredResidence(managedResidence);
					} else if(managedResidence.getPerson().getPreferredResidence() != null && managedResidence.getPerson().getPreferredResidence().equals(managedResidence)) {
						managedResidence.getPerson().setPreferredResidence(null);
					}

					em.getTransaction().commit();

					setResult(managedResidence);
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
}

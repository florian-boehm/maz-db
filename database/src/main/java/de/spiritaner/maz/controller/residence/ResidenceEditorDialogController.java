package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.model.Residence;
import de.spiritaner.maz.view.dialog.EditorDialog;
import javafx.scene.layout.GridPane;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;

@EditorDialog.Annotation(fxmlFile = "/fxml/residence/residence_editor_dialog.fxml", objDesc = "$residence")
public class ResidenceEditorDialogController extends EditorDialogController<Residence> {

	final static Logger logger = Logger.getLogger(ResidenceEditorDialogController.class);

	public GridPane personEditor;
	public PersonEditorController personEditorController;
	public GridPane addressEditor;
	public AddressEditorController addressEditorController;
	public GridPane residenceEditor;
	public ResidenceEditorController residenceEditorController;

	@Override
	public void bind(Residence residence) {
		residenceEditorController.residence.bindBidirectional(identifiable);
		personEditorController.person.bindBidirectional(residence.person);
		personEditorController.readOnly.bind(residence.person.isNotNull());
		addressEditorController.address.bindBidirectional(residence.address);
	}

	@Override
	protected boolean allValid() {
		boolean addressValid = addressEditorController.isValid();
		boolean personValid = personEditorController.isValid();
		boolean residenceValid = residenceEditorController.isValid();

		return addressValid && personValid && residenceValid;
	}

	@Override
	protected void preSave(Residence managedResidence, EntityManager em) {
		if (residenceEditorController.getPreferredResidence().isSelected()) {
			managedResidence.getPerson().setPreferredResidence(managedResidence);
		} else if(managedResidence.getPerson().getPreferredResidence() != null && managedResidence.getPerson().getPreferredResidence().equals(managedResidence)) {
			managedResidence.getPerson().setPreferredResidence(null);
		}
	}
}

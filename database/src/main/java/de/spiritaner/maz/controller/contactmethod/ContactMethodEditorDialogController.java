package de.spiritaner.maz.controller.contactmethod;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.model.ContactMethod;
import de.spiritaner.maz.view.dialog.EditorDialog;
import javafx.scene.layout.GridPane;
import org.apache.log4j.Logger;

@EditorDialog.Annotation(fxmlFile = "/fxml/contactmethod/contactmethod_editor_dialog.fxml", objDesc = "$contact_method")
public class ContactMethodEditorDialogController extends EditorDialogController<ContactMethod> {

	final static Logger logger = Logger.getLogger(ContactMethodEditorDialogController.class);

	public GridPane personEditor;
	public PersonEditorController personEditorController;
	public GridPane contactMethodEditor;
	public ContactMethodEditorController contactMethodEditorController;

	@Override
	protected void bind(ContactMethod contactMethod) {
		contactMethodEditorController.contactMethod.bindBidirectional(identifiable);

		personEditorController.readOnly.bind(contactMethod.person.isNotNull());
		personEditorController.person.bindBidirectional(contactMethod.person);
	}

	@Override
	protected boolean allValid() {
		boolean personValid = personEditorController.isValid();
		boolean contactMethodValid = contactMethodEditorController.isValid();

		return personValid && contactMethodValid;
	}
}

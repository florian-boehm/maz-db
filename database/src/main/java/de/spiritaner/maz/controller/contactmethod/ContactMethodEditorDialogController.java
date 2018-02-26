package de.spiritaner.maz.controller.contactmethod;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.model.ContactMethod;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.view.dialog.EditorDialog;
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

@EditorDialog.Annotation(fxmlFile = "/fxml/contactmethod/contactmethod_editor_dialog.fxml", objDesc = "Kontaktweg")
public class ContactMethodEditorDialogController extends EditorDialogController<ContactMethod> {

	final static Logger logger = Logger.getLogger(ContactMethodEditorDialogController.class);

	public GridPane personEditor;
	public PersonEditorController personEditorController;
	public GridPane contactMethodEditor;
	public ContactMethodEditorController contactMethodEditorController;

	@Override
	protected void init() {
		identifiable.addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				// Check if a person is already set in this residence
				if (newValue.getPerson() != null) {
					personEditorController.person.set(newValue.getPerson());
					personEditorController.readOnly.set(true);
				}

				contactMethodEditorController.setAll(newValue);
			}
		});
	}

	@Override
	protected boolean allValid() {
		boolean personValid = personEditorController.isValid();
		boolean contactMethodValid = contactMethodEditorController.isValid();

		return personValid && contactMethodValid;
	}
}

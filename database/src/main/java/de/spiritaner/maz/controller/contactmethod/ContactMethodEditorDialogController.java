package de.spiritaner.maz.controller.contactmethod;

import de.spiritaner.maz.controller.EditorController;
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
public class ContactMethodEditorDialogController extends EditorController<ContactMethod> {

	final static Logger logger = Logger.getLogger(ContactMethodEditorDialogController.class);

	@FXML
	private Button saveContactMethodButton;
	@FXML
	private Text titleText;
	@FXML
	private GridPane personEditor;
	@FXML
	private PersonEditorController personEditorController;
	@FXML
	private GridPane contactMethodEditor;
	@FXML
	private ContactMethodEditorController contactMethodEditorController;

	@Override
	public void setIdentifiable(ContactMethod contactMethod) {
		super.setIdentifiable(contactMethod);

		if (contactMethod != null) {
			// Check if a person is already set in this residence
			if (contactMethod.getPerson() != null) {
				personEditorController.setAll(contactMethod.getPerson());
				personEditorController.setReadonly(true);
			}

			contactMethodEditorController.setAll(contactMethod);

			if (contactMethod.getId() != 0L) {
				titleText.setText(getIdentifiableName() + " bearbeiten");
				saveContactMethodButton.setText("Speichern");
			} else {
				titleText.setText(getIdentifiableName() + " anlegen");
				saveContactMethodButton.setText("Anlegen");
			}
		}
	}

	@Override
	public void onReopen() {
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
	}

	public void saveContactMethod(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			boolean personValid = personEditorController.isValid();
			boolean contactMethodValid = contactMethodEditorController.isValid();

			if (personValid && contactMethodValid) {
				EntityManager em = CoreDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				getIdentifiable().setPerson(personEditorController.getAll(getIdentifiable().getPerson()));
				contactMethodEditorController.getAll(getIdentifiable());

				try {
					ContactMethod managedContactMethod = (!em.contains(getIdentifiable())) ? em.merge(getIdentifiable()) : getIdentifiable();
					em.getTransaction().commit();
					setResult(managedContactMethod);
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

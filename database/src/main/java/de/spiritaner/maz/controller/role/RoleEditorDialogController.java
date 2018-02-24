package de.spiritaner.maz.controller.role;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.model.Role;
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

@EditorDialog.Annotation(fxmlFile = "/fxml/role/role_editor_dialog.fxml", objDesc = "Funktion")
public class RoleEditorDialogController extends EditorDialogController<Role> {

	final static Logger logger = Logger.getLogger(RoleEditorDialogController.class);

	@FXML
	private Button saveRoleButton;
	@FXML
	private Text titleText;
	@FXML
	private GridPane personEditor;
	@FXML
	private PersonEditorController personEditorController;
	@FXML
	private GridPane roleEditor;
	@FXML
	private RoleEditorController roleEditorController;

	@Override
	public void setIdentifiable(Role role) {
		super.setIdentifiable(role);

		if (role != null) {
			// Check if a person is already set in this role
			if (role.getPerson() != null) {
				personEditorController.person.set(role.getPerson());
				personEditorController.readOnly.set(true);
			}

			roleEditorController.setAll(role);

			if (role.getId() != 0L) {
				titleText.setText("Funktion bearbeiten");
				saveRoleButton.setText("Speichern");
			} else {
				titleText.setText("Funktion anlegen");
				saveRoleButton.setText("Anlegen");
			}
		}
	}

	@Override
	public void onReopen() {
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
	}

	public void saveRole(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			boolean personValid = personEditorController.isValid();
			boolean roleValid = roleEditorController.isValid();

			if (personValid && roleValid) {
				EntityManager em = CoreDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				roleEditorController.getAll(getIdentifiable());

				try {
					Role managedRole = (!em.contains(getIdentifiable())) ? em.merge(getIdentifiable()) : getIdentifiable();
					em.getTransaction().commit();

					setResult(managedRole);
					requestClose();
					// Add backwards relationship too
					//if(!managedRole.getPerson().getRoles().contains(managedRole)) managedRole.getPerson().getRoles().add(managedRole);
					//getStage().close();
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

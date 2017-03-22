package de.spiritaner.maz.controller.role;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.model.Role;
import de.spiritaner.maz.util.DataDatabase;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/role/role_editor_dialog.fxml", objDesc = "Funktion")
public class RoleEditorDialogController extends EditorController<Role> {

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

	private Stage stage;
	private Role role;

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void setIdentifiable(Role obj) {
		setRole(obj);
	}

	@Override
	public void onReopen() {
	}

	public void setRole(Role role) {
		this.role = role;

		if (role != null) {
			// Check if a person is already set in this role
			if (role.getPerson() != null) {
				personEditorController.setAll(role.getPerson());
				personEditorController.setReadonly(true);
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
	public void initialize(URL url, ResourceBundle resourceBundle) {
	}

	public void saveRole(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			boolean personValid = personEditorController.isValid();
			boolean roleValid = roleEditorController.isValid();

			if (personValid && roleValid) {
				EntityManager em = DataDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				role.setPerson(personEditorController.getAll(role.getPerson()));
				roleEditorController.getAll(role);

				try {
					if (!em.contains(role)) em.merge(role);

					// Add backwards relationship too
					if(!role.getPerson().getRoles().contains(role)) role.getPerson().getRoles().add(role);

					em.getTransaction().commit();
					stage.close();
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
		Platform.runLater(() -> stage.close());
	}
}

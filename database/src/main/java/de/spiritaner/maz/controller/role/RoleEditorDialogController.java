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

@EditorDialog.Annotation(fxmlFile = "/fxml/role/role_editor_dialog.fxml", objDesc = "$role")
public class RoleEditorDialogController extends EditorDialogController<Role> {

	final static Logger logger = Logger.getLogger(RoleEditorDialogController.class);

	public GridPane personEditor;
	public PersonEditorController personEditorController;
	public GridPane roleEditor;
	public RoleEditorController roleEditorController;

	@Override
	protected void bind(Role role) {
		personEditorController.person.bindBidirectional(role.person);
		personEditorController.readOnly.bind(role.person.isNotNull());
		roleEditorController.role.bindBidirectional(identifiable);
	}

	@Override
	protected boolean allValid() {
		boolean personValid = personEditorController.isValid();
		boolean roleValid = roleEditorController.isValid();

		return personValid && roleValid;
	}
}

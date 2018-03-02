package de.spiritaner.maz.controller.role;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.model.Role;
import de.spiritaner.maz.view.dialog.EditorDialog;
import javafx.scene.layout.GridPane;
import org.apache.log4j.Logger;

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

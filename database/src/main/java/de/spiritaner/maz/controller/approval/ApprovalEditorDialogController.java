package de.spiritaner.maz.controller.approval;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.model.Approval;
import de.spiritaner.maz.view.dialog.EditorDialog;
import javafx.scene.layout.GridPane;
import org.apache.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;

import javax.persistence.EntityManager;

@EditorDialog.Annotation(fxmlFile = "/fxml/approval/approval_editor_dialog.fxml", objDesc = "$approval")
public class ApprovalEditorDialogController extends EditorDialogController<Approval> {


	public GridPane personEditor;
	public PersonEditorController personEditorController;
	public GridPane approvalEditor;
	public ApprovalEditorController approvalEditorController;

	@Override
	protected void bind(Approval approval) {
		approvalEditorController.approval.bindBidirectional(identifiable);

		personEditorController.readOnly.bind(approval.person.isNotNull());
		personEditorController.person.bindBidirectional(approval.person);
	}

	@Override
	protected boolean allValid() {
		boolean personValid = personEditorController.isValid();
		boolean approvalValid = (approvalEditorController.readOnly.get()) || approvalEditorController.isValid();

		return personValid && approvalValid;
	}
}

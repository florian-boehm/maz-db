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

	final static Logger logger = Logger.getLogger(ApprovalEditorDialogController.class);

	public GridPane personEditor;
	public PersonEditorController personEditorController;
	public GridPane approvalEditor;
	public ApprovalEditorController approvalEditorController;
	public ToggleSwitch photoApprovalToggleSwitch;
	public ToggleSwitch privacyPolicyToggleSwitch;
	public ToggleSwitch newsletterToggleSwitch;

	@Override
	protected void bind(Approval approval) {
		privacyPolicyToggleSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
			//photoApprovalToggleSwitch.setDisable(!newValue);
			newsletterToggleSwitch.setDisable(!newValue);
		});

		identifiable.addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				// Check if a person is already set in this residence
				if (newValue.getPerson() != null) {
					personEditorController.person.set(newValue.getPerson());
					personEditorController.readOnly.set(true);

					newValue.getPerson().getApprovals().forEach(singleApproval -> {
						switch (singleApproval.getApprovalType().getId().intValue()) {
							case 1:
								privacyPolicyToggleSwitch.setSelected(singleApproval.isApproved());
								break;
							case 2:
								photoApprovalToggleSwitch.setSelected(singleApproval.isApproved());
								break;
							case 3:
								newsletterToggleSwitch.setSelected(singleApproval.isApproved());
								break;
						}
					});
				}

				// not needed: photoApprovalToggleSwitch.setDisable(!privacyPolicyToggleSwitch.isSelected());
				newsletterToggleSwitch.setDisable(!privacyPolicyToggleSwitch.isSelected());

				if (newValue.getApprovalType() == null || newValue.getApprovalType().getId() > 100) {
					approvalEditorController.approval.set(newValue);
				} else {
					approvalEditorController.readOnly.set(true);
				}
			}
		});
	}

	@Override
	protected boolean allValid() {
		boolean personValid = personEditorController.isValid();
		boolean approvalValid = (approvalEditorController.readOnly.get()) || approvalEditorController.isValid();

		return personValid && approvalValid;
	}

	@Override
	protected void preSave(EntityManager em) {
		for(Approval approval : identifiable.get().person.get().getApprovals()) {
			Approval managedApproval = (!em.contains(approval)) ? em.merge(approval) : approval;

			switch (approval.getApprovalType().getId().intValue()) {
				case 1:
					managedApproval.setApproved(privacyPolicyToggleSwitch.isSelected());
					approval.setApproved(privacyPolicyToggleSwitch.isSelected());
					break;
				case 2:
					approval.setApproved(photoApprovalToggleSwitch.isSelected());
					managedApproval.setApproved(photoApprovalToggleSwitch.isSelected());
					break;
				case 3:
					approval.setApproved(privacyPolicyToggleSwitch.isSelected() && newsletterToggleSwitch.isSelected());
					managedApproval.setApproved(privacyPolicyToggleSwitch.isSelected() && newsletterToggleSwitch.isSelected());
					break;
			}
		}

		// TODO Check if this is correctly implemented
		/*if(!approvalEditorController.isReadOnly()) approvalEditorController.getAll(getIdentifiable());

				try {
					if(!approvalEditorController.isReadOnly()) {
						Approval managedApproval = (!em.contains(getIdentifiable())) ? em.merge(getIdentifiable()) : getIdentifiable();
						em.getTransaction().commit();
						setResult(managedApproval);
					} else {
						em.getTransaction().commit();
					}

					requestClose();
				} catch (PersistenceException e) {
					em.getTransaction().rollback();
					logger.warn(e);
				} finally {
					em.close();
				}
			}
		});*/
	}
}

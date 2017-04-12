package de.spiritaner.maz.controller.approval;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.model.Approval;
import de.spiritaner.maz.util.DataDatabase;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/approval/approval_editor_dialog.fxml", objDesc = "Einwilligung")
public class ApprovalEditorDialogController extends EditorController<Approval> {

	final static Logger logger = Logger.getLogger(ApprovalEditorDialogController.class);

	@FXML
	private Button saveApprovalButton;
	@FXML
	private Text titleText;
	@FXML
	private GridPane personEditor;
	@FXML
	private PersonEditorController personEditorController;
	@FXML
	private GridPane approvalEditor;
	@FXML
	private ApprovalEditorController approvalEditorController;
	@FXML
	private ToggleSwitch photoApprovalToggleSwitch;
	@FXML
	private ToggleSwitch privacyPolicyToggleSwitch;
	@FXML
	private ToggleSwitch newsletterToggleSwitch;

	@Override
	public void setIdentifiable(Approval approval) {
		super.setIdentifiable(approval);

		if (approval != null) {
			// Check if a person is already set in this residence
			if (approval.getPerson() != null) {
				personEditorController.setAll(approval.getPerson());
				personEditorController.setReadonly(true);

				logger.info("Approval List: "+getIdentifiable().getPerson().getApprovals());
				logger.info("Size of approval list: "+getIdentifiable().getPerson().getApprovals().size());
				for (Approval tmpApproval : approval.getPerson().getApprovals()) {
					switch (tmpApproval.getApprovalType().getId().intValue()) {
						case 1:
							photoApprovalToggleSwitch.setSelected(tmpApproval.isApproved());
							break;
						case 2:
							privacyPolicyToggleSwitch.setSelected(tmpApproval.isApproved());
							break;
						case 3:
							newsletterToggleSwitch.setSelected(tmpApproval.isApproved());
							break;
					}
				}
			}

			if (approval.getApprovalType() == null || approval.getApprovalType().getId() > 100) {
				approvalEditorController.setAll(approval);
			} else {
				approvalEditorController.setReadonly(true);
			}

			if (approval.getId() != 0L) {
				titleText.setText("Einwilligung bearbeiten");
				saveApprovalButton.setText("Speichern");
			} else {
				titleText.setText("Einwilligung anlegen");
				saveApprovalButton.setText("Anlegen");
			}
		}
	}

	@Override
	public void onReopen() {
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
	}

	public void saveApproval(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			boolean personValid = personEditorController.isValid();
			boolean approvalValid = (approvalEditorController.isReadOnly()) || approvalEditorController.isValid();

			if (personValid && approvalValid) {
				EntityManager em = DataDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				logger.info("Person: "+getIdentifiable().getPerson());
				getIdentifiable().setPerson(personEditorController.getAll(getIdentifiable().getPerson()));

				logger.info("Approval List: "+getIdentifiable().getPerson().getApprovals());
				logger.info("Size of approval list: "+getIdentifiable().getPerson().getApprovals().size());
				for (Approval tmpApproval : getIdentifiable().getPerson().getApprovals()) {
					switch (tmpApproval.getApprovalType().getId().intValue()) {
						case 1:
							tmpApproval.setApproved(photoApprovalToggleSwitch.isSelected());
							break;
						case 2:
							tmpApproval.setApproved(privacyPolicyToggleSwitch.isSelected());
							break;
						case 3:
							tmpApproval.setApproved(newsletterToggleSwitch.isSelected());
							break;
					}

					em.merge(tmpApproval);
				}

				approvalEditorController.getAll(getIdentifiable());

				try {
					if(!approvalEditorController.isReadOnly()) {
						Approval managedApproval = (!em.contains(getIdentifiable())) ? em.merge(getIdentifiable()) : getIdentifiable();
						em.getTransaction().commit();
						setResult(managedApproval);
					}

					requestClose();
					// Add backwards relationship too
					//if(!approval.getPerson().getApprovals().contains(approval)) approval.getPerson().getApprovals().add(approval);
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

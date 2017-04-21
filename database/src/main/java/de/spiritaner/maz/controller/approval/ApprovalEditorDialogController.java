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
import javax.persistence.TypedQuery;
import javax.xml.crypto.Data;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

				approval.getPerson().getApprovals().forEach(singleApproval -> {
					switch (singleApproval.getApprovalType().getId().intValue()) {
						case 1:
							photoApprovalToggleSwitch.setSelected(singleApproval.isApproved());
							break;
						case 2:
							privacyPolicyToggleSwitch.setSelected(singleApproval.isApproved());
							break;
						case 3:
							newsletterToggleSwitch.setSelected(singleApproval.isApproved());
							break;
					}
				});
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

				getIdentifiable().setPerson(personEditorController.getAll(getIdentifiable().getPerson()));

				getIdentifiable().getPerson().getApprovals().forEach(approval -> {
					switch (approval.getApprovalType().getId().intValue()) {
						case 1:
							approval.setApproved(photoApprovalToggleSwitch.isSelected());
							break;
						case 2:
							approval.setApproved(privacyPolicyToggleSwitch.isSelected());
							break;
						case 3:
							approval.setApproved(newsletterToggleSwitch.isSelected());
							break;
					}
				});

				if(!approvalEditorController.isReadOnly()) approvalEditorController.getAll(getIdentifiable());

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
		});
	}

	public void closeDialog(ActionEvent actionEvent) {
		Platform.runLater(() -> getStage().close());
	}
}

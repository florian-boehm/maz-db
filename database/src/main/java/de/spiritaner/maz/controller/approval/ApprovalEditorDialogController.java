package de.spiritaner.maz.controller.approval;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.model.Approval;
import de.spiritaner.maz.util.DataDatabase;
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

    private Approval approval;

    @Override
    public void setIdentifiable(Approval obj) {
        setApproval(obj);
    }

    @Override
    public void onReopen() {
    }

    public void setApproval(Approval approval) {
        this.approval = approval;

        if (approval != null) {
            // Check if a person is already set in this residence
            if (approval.getPerson() != null) {
                personEditorController.setAll(approval.getPerson());
                personEditorController.setReadonly(true);
            }

            approvalEditorController.setAll(approval);

            if(approval.getId() != 0L) {
                titleText.setText("Einwilligung bearbeiten");
                saveApprovalButton.setText("Speichern");
            } else {
                titleText.setText("Einwilligung anlegen");
                saveApprovalButton.setText("Anlegen");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void saveApproval(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            boolean personValid = personEditorController.isValid();
            boolean approvalValid = approvalEditorController.isValid();

            if (personValid && approvalValid) {
                EntityManager em = DataDatabase.getFactory().createEntityManager();
                em.getTransaction().begin();

					 approval.setPerson(personEditorController.getAll(approval.getPerson()));
                approvalEditorController.getAll(approval);

                try {
                    if (!em.contains(approval)) em.merge(approval);
                    em.getTransaction().commit();

                    // Add backwards relationship too
                    if(!approval.getPerson().getApprovals().contains(approval)) approval.getPerson().getApprovals().add(approval);

                    getStage().close();
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

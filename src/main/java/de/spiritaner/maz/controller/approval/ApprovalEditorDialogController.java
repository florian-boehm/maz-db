package de.spiritaner.maz.controller.approval;

import de.spiritaner.maz.controller.Controller;
import de.spiritaner.maz.controller.contactmethod.ContactMethodEditorController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.model.Approval;
import de.spiritaner.maz.model.ContactMethod;
import de.spiritaner.maz.util.DataDatabase;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.ResourceBundle;

public class ApprovalEditorDialogController implements Initializable, Controller {

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

    private Stage stage;
    private Approval approval;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
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

                Approval tmpApproval = (approval.getId() != 0L) ? em.find(Approval.class, approval.getId()) : approval;

                if(tmpApproval.getPerson() == null) {
                    tmpApproval.setPerson(personEditorController.getAll(tmpApproval.getPerson()));
                }

                tmpApproval = approvalEditorController.getAll(tmpApproval);

                try {
                    if (!em.contains(tmpApproval)) em.persist(tmpApproval);

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

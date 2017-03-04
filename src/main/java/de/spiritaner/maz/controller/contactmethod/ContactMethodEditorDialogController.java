package de.spiritaner.maz.controller.contactmethod;

import de.spiritaner.maz.controller.Controller;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.controller.residence.AddressEditorController;
import de.spiritaner.maz.controller.residence.ResidenceEditorController;
import de.spiritaner.maz.model.ContactMethod;
import de.spiritaner.maz.model.Residence;
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

public class ContactMethodEditorDialogController implements Initializable, Controller {

    final static Logger logger = Logger.getLogger(ContactMethodEditorDialogController.class);

    @FXML
    private Button saveContactMethodButton;
    @FXML
    private Text titleText;
    @FXML
    private GridPane personEditor;
    @FXML
    private PersonEditorController personEditorController;
    @FXML
    private GridPane contactMethodEditor;
    @FXML
    private ContactMethodEditorController contactMethodEditorController;

    private Stage stage;
    private ContactMethod contactMethod;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void onReopen() {
    }

    public void setContactMethod(ContactMethod contactMethod) {
        this.contactMethod = contactMethod;

        if (contactMethod != null) {
            // Check if a person is already set in this residence
            if (contactMethod.getPerson() != null) {
                personEditorController.setAll(contactMethod.getPerson());
                personEditorController.setReadonly(true);
            }

            contactMethodEditorController.setAll(contactMethod);

            if(contactMethod.getId() != 0L) {
                titleText.setText("Kontaktweg bearbeiten");
                saveContactMethodButton.setText("Speichern");
            } else {
                titleText.setText("Kontaktweg anlegen");
                saveContactMethodButton.setText("Anlegen");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void saveContactMethod(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            boolean personValid = personEditorController.isValid();
            boolean contactMethodValid = contactMethodEditorController.isValid();

            if (personValid && contactMethodValid) {
                EntityManager em = DataDatabase.getFactory().createEntityManager();
                em.getTransaction().begin();

                ContactMethod tmpContactMethod = (contactMethod.getId() != 0L) ? em.find(ContactMethod.class, contactMethod.getId()) : contactMethod;

                if(tmpContactMethod.getPerson() == null) {
                    tmpContactMethod.setPerson(personEditorController.getAll(tmpContactMethod.getPerson()));
                }

                tmpContactMethod = contactMethodEditorController.getAll(tmpContactMethod);

                try {
                    if (!em.contains(tmpContactMethod)) em.persist(tmpContactMethod);

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

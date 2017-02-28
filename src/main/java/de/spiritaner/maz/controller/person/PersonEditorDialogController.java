package de.spiritaner.maz.controller.person;

import de.spiritaner.maz.controller.Controller;
import de.spiritaner.maz.model.Person;
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

public class PersonEditorDialogController implements Initializable, Controller {

    final static Logger logger = Logger.getLogger(PersonEditorDialogController.class);

    @FXML
    private Text titleText;
    @FXML
    private GridPane personEditor;
    @FXML
    private PersonEditorController personEditorController;
    @FXML
    private Button savePersonButton;

    private Person person;
    private Stage stage;

    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setPerson(Person person) {
        this.person = person;

        if (person != null) {
            personEditorController.setAll(person);

            if(person.getId() != 0L) {
                titleText.setText("Person bearbeiten");
                savePersonButton.setText("Speichern");
            } else {
                titleText.setText("Person anlegen");
                savePersonButton.setText("Anlegen");
            }
        }
    }

    public void closeDialog(ActionEvent actionEvent) {
        Platform.runLater(() -> stage.close());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void onReopen() {

    }

    public void savePerson(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            // Check if the first name, family name and birthday are valid
            boolean validation = personEditorController.isValid();

            if (validation) {
                EntityManager em = DataDatabase.getFactory().createEntityManager();
                em.getTransaction().begin();

                Person tmpPerson = personEditorController.getAll((person.getId() != null) ? em.find(Person.class, person.getId()) : person);

                try {
                    // Persist only if the person has not existed before
                    if (!em.contains(tmpPerson)) em.persist(tmpPerson);

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
}

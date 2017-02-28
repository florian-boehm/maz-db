package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.util.DataDatabase;
import de.spiritaner.maz.util.TextValidator;
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

public class AddressEditorDialogController implements Initializable {

    final static Logger logger = Logger.getLogger(AddressEditorDialogController.class);

    @FXML
    private Text titleText;
    @FXML
    private Button saveAddressButton;
    @FXML
    private GridPane addressEditor;
    @FXML
    private AddressEditorController addressEditorController;

    private Address address;
    private Stage stage;

    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setAddress(Address address) {
        this.address = address;

        if (address != null) {
            titleText.setText("Adresse bearbeiten");
            saveAddressButton.setText("Speichern");
            addressEditorController.setAll(address);
        } else {
            titleText.setText("Adresse anlegen");
            saveAddressButton.setText("Anlegen");
        }
    }

    public void saveAddress(ActionEvent actionEvent) {
        // Check if the fields according to the model are valid
        boolean validation = addressEditorController.isValid();

        if(validation) {
            EntityManager em = DataDatabase.getFactory().createEntityManager();
            em.getTransaction().begin();

            Address tmpAddress = addressEditorController.getAll((address == null) ? new Address() : em.find(Address.class, address.getId()));

            try {
                // Persist only if the person has not existed before
                if(address == null) em.persist(tmpAddress);

                em.getTransaction().commit();
                stage.close();
            } catch (PersistenceException e) {
                em.getTransaction().rollback();
                logger.warn(e);
            } finally {
                em.close();
            }
        }
    }

    public void closeDialog(ActionEvent actionEvent) {
        Platform.runLater(() -> stage.close());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}

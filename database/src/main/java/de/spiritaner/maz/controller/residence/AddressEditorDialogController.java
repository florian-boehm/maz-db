package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.view.dialog.EditorDialog;
import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.util.database.CoreDatabase;
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

@EditorDialog.Annotation(fxmlFile = "/fxml/residence/address_editor_dialog.fxml", objDesc = "Adresse")
public class AddressEditorDialogController extends EditorController<Address> {

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
            EntityManager em = CoreDatabase.getFactory().createEntityManager();
            em.getTransaction().begin();

            Address tmpAddress = addressEditorController.getAll((address == null) ? new Address() : em.find(Address.class, address.getId()));

            try {
                // Persist only if the person has not existed before
                if(address == null) em.persist(tmpAddress);

                em.getTransaction().commit();
                getStage().close();
            } catch (PersistenceException e) {
                em.getTransaction().rollback();
                logger.warn(e);
            } finally {
                em.close();
            }
        }
    }

    public void closeDialog(ActionEvent actionEvent) {
        Platform.runLater(() -> getStage().close());
    }

    @Override
    public void setIdentifiable(Address obj) {
        setAddress(obj);
    }

    @Override
    public void onReopen() {

    }
}

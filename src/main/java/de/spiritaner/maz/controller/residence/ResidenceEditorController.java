package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.controller.meta.GenderEditorController;
import de.spiritaner.maz.controller.meta.ResidenceTypeEditorController;
import de.spiritaner.maz.model.Residence;
import de.spiritaner.maz.model.meta.Gender;
import de.spiritaner.maz.model.meta.ResidenceType;
import de.spiritaner.maz.util.DataDatabase;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.controlsfx.control.ToggleSwitch;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class ResidenceEditorController implements Initializable {

    @FXML private
    Button addNewResidenceTypeButton;
    @FXML
    private ToggleSwitch preferredResidence;

    @FXML
    private ComboBox<ResidenceType> residenceTypeComboBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadResidenceTypes();
    }

    public void setAll(Residence residence) {
        preferredResidence.setSelected(residence.getPreferredAddress());
        residenceTypeComboBox.setValue(residence.getResidenceType());
    }

    public Residence getAll(Residence residence) {
        if(residence == null) residence = new Residence();
        residence.setPreferredAddress(preferredResidence.isSelected());
        residence.setResidenceType(residenceTypeComboBox.getValue());
        return residence;
    }

    public void setReadonly(boolean readonly) {
        preferredResidence.setDisable(readonly);
        residenceTypeComboBox.setDisable(readonly);
    }

    public void loadResidenceTypes() {
        EntityManager em = DataDatabase.getFactory().createEntityManager();
        Collection<ResidenceType> result = em.createNamedQuery("ResidenceType.findAll", ResidenceType.class).getResultList();

        ResidenceType selectedBefore = residenceTypeComboBox.getValue();
        residenceTypeComboBox.getItems().clear();
        residenceTypeComboBox.getItems().addAll(FXCollections.observableArrayList(result));
        residenceTypeComboBox.setValue(selectedBefore);
    }

    public boolean isValid() {
        // TODO Validate the residence specific fields
        return true;
    }

    public void addNewResidenceType(ActionEvent actionEvent) {
        new ResidenceTypeEditorController().create(actionEvent);

        loadResidenceTypes();
    }
}

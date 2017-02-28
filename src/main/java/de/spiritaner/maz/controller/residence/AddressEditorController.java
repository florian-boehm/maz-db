package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.util.TextValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AddressEditorController implements Initializable {

    @FXML
    TextField streetField;
    @FXML
    TextField houseNumberField;
    @FXML
    TextField postCodeField;
    @FXML
    TextField cityField;
    @FXML
    TextField stateField;
    @FXML
    TextField countryField;
    @FXML
    TextField additionField;

    private TextValidator streetFieldValidator;
    private TextValidator houseNumberFieldValidator;
    private TextValidator postCodeFieldValidator;
    private TextValidator cityFieldValidator;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        streetFieldValidator = TextValidator.create(streetField).fieldName("Straße").notEmpty(true).textChanged();
        houseNumberFieldValidator = TextValidator.create(houseNumberField).fieldName("Hausnummer").notEmpty(true).textChanged();
        postCodeFieldValidator = TextValidator.create(postCodeField).fieldName("Postleitzahl").max(10).notEmpty(true).textChanged();
        cityFieldValidator = TextValidator.create(cityField).fieldName("Stadt").notEmpty(true).textChanged();
    }

    public void setAll(Address address) {
        streetField.setText(address.getStreet());
        houseNumberField.setText(address.getHouseNumber());
        postCodeField.setText(address.getPostCode());
        cityField.setText(address.getCity());
        stateField.setText(address.getState());
        countryField.setText(address.getCountry());
        additionField.setText(address.getAddition());
    }

    public Address getAll(Address address) {
        if(address == null) address = new Address();
        address.setStreet(streetField.getText());
        address.setHouseNumber(houseNumberField.getText());
        address.setPostCode(postCodeField.getText());
        address.setCity(cityField.getText());
        address.setState(stateField.getText());
        address.setCountry(countryField.getText());
        address.setAddition(additionField.getText());
        return address;
    }

    public void setReadonly(boolean readonly) {
        streetField.setDisable(readonly);
        houseNumberField.setDisable(readonly);
        postCodeField.setDisable(readonly);
        cityField.setDisable(readonly);
        stateField.setDisable(readonly);
        countryField.setDisable(readonly);
        additionField.setDisable(readonly);
    }

    public boolean isValid() {
        boolean streetValid = streetFieldValidator.validate();
        boolean houseNumberValid = houseNumberFieldValidator.validate();
        boolean postCodeValid = postCodeFieldValidator.validate();
        boolean cityValid = cityFieldValidator.validate();

        return  streetValid && houseNumberValid && postCodeValid && cityValid;
    }
}

package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.util.validator.TextValidator;
import de.spiritaner.maz.view.binding.AutoBinder;
import de.spiritaner.maz.view.component.BindableTextField;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AddressEditorController extends EditorController {

	public ObjectProperty<Address> address = new SimpleObjectProperty<>();

	public BindableTextField streetField;
	public BindableTextField houseNumberField;
	public BindableTextField postCodeField;
	public BindableTextField cityField;
	public BindableTextField stateField;
	public BindableTextField countryField;
	public BindableTextField additionField;

	private TextValidator streetFieldValidator;
	private TextValidator houseNumberFieldValidator;
	private TextValidator postCodeFieldValidator;
	private TextValidator cityFieldValidator;

	public void initialize(URL url, ResourceBundle resourceBundle) {
		AutoBinder ab = new AutoBinder(this);
		address.addListener((observableValue, oldValue, newValue) -> ab.rebindAll());

		streetFieldValidator = TextValidator.create(streetField).fieldName("Straße").notEmpty(true).validateOnChange();
		houseNumberFieldValidator = TextValidator.create(houseNumberField).fieldName("Hausnummer").notEmpty(true).validateOnChange();
		postCodeFieldValidator = TextValidator.create(postCodeField).fieldName("Postleitzahl").max(10).notEmpty(true).validateOnChange();
		cityFieldValidator = TextValidator.create(cityField).fieldName("Stadt").notEmpty(true).validateOnChange();
	}

	@Override
	public boolean isValid() {
		boolean streetValid = streetFieldValidator.validate();
		boolean houseNumberValid = houseNumberFieldValidator.validate();
		boolean postCodeValid = postCodeFieldValidator.validate();
		boolean cityValid = cityFieldValidator.validate();

		return streetValid && houseNumberValid && postCodeValid && cityValid;
	}
}

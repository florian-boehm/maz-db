package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.util.validator.PopOverVisitor;
import de.spiritaner.maz.view.component.BindableTextField;
import de.spiritaner.maz.util.validator.NotEmpty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

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

	public void initialize(URL url, ResourceBundle resourceBundle) {
		// Bind all bindable fields to the bindable property
		autoBinder.register(this);
		address.addListener((observableValue, oldValue, newValue) -> super.autoBinder.rebindAll());

		// Change the validator visitor to PopOver and add Validations as well as change listeners
		autoValidator.visitor = new PopOverVisitor();
		autoValidator.add(new NotEmpty(streetField, guiText.getString("street")));
		autoValidator.add(new NotEmpty(houseNumberField, guiText.getString("house_number")));
		autoValidator.add(new NotEmpty(cityField, guiText.getString("city")));
		autoValidator.add(new NotEmpty(postCodeField, guiText.getString("post_code")));
		autoValidator.validateOnChange(streetField);
		autoValidator.validateOnChange(houseNumberField);
		autoValidator.validateOnChange(cityField);
		autoValidator.validateOnChange(cityField);

		// TODO Check post code field for max length of 10 numbers!
	}
}

package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.view.component.BindableTextField;
import de.spiritaner.maz.view.validation.NotEmpty;
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
		super.autoBinder.register(this);
		address.addListener((observableValue, oldValue, newValue) -> super.autoBinder.rebindAll());

		super.autoValidator.register(streetField, new NotEmpty(streetField), true);
		//super.autoValidator.register(houseNumberField, new NotEmpty(), true);

		/*streetFieldValidator = TextValidator.create(streetField).fieldName("Straße").notEmpty(true).validateOnChange();
		houseNumberFieldValidator = TextValidator.create(houseNumberField).fieldName("Hausnummer").notEmpty(true).validateOnChange();
		postCodeFieldValidator = TextValidator.create(postCodeField).fieldName("Postleitzahl").max(10).notEmpty(true).validateOnChange();
		cityFieldValidator = TextValidator.create(cityField).fieldName("Stadt").notEmpty(true).validateOnChange();
		*/
	}
}

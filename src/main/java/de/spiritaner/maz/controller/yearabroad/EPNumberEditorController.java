package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.model.EPNumber;
import de.spiritaner.maz.util.validator.TextValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class EPNumberEditorController implements Initializable {

	@FXML
	private TextField epNumberField;
	@FXML
	private TextField descriptionField;

	private TextValidator epNumberValidator;
	private TextValidator descriptionFieldValidator;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		epNumberValidator = TextValidator.create(epNumberField).fieldName("EP-Nummer").notEmpty(true).validateOnChange();
		descriptionFieldValidator = TextValidator.create(descriptionField).fieldName("Beschreibung").notEmpty(true).validateOnChange();
	}

	public void setAll(EPNumber epNumber) {
		epNumberField.setText(String.valueOf(epNumber.getNumber()));
		descriptionField.setText(epNumber.getDescription());
	}

	public EPNumber getAll(EPNumber epNumber) {
		if(epNumber == null) epNumber = new EPNumber();
		epNumber.setNumber(Integer.parseInt(epNumberField.getText()));
		epNumber.setDescription(descriptionField.getText());
		return epNumber;
	}

	public void setReadonly(boolean readonly) {
		epNumberField.setDisable(readonly);
		descriptionField.setDisable(readonly);
	}

	public boolean isValid() {
		boolean epNumberValid = epNumberValidator.validate();
		boolean descriptionValid = descriptionFieldValidator.validate();

		return epNumberValid && descriptionValid;
	}
}

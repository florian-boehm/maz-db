package de.spiritaner.maz.controller.experienceabroad;

import de.spiritaner.maz.model.ExperienceAbroad;
import de.spiritaner.maz.util.factory.DatePickerFormatter;
import de.spiritaner.maz.util.validator.DateValidator;
import de.spiritaner.maz.util.validator.TextValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class ExperienceAbroadEditorController implements Initializable {

	final static Logger logger = Logger.getLogger(ExperienceAbroadEditorController.class);

	@FXML private TextField communityField;
	@FXML private TextArea detailsTextArea;
	@FXML private TextField locationField;
	@FXML private DatePicker fromDatePicker;
	@FXML private DatePicker toDatePicker;

	TextValidator communityFieldValidator;
	TextValidator locationFieldValidator;
	DateValidator fromDateValidator;
	DateValidator toDateValidator;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		communityFieldValidator = TextValidator.create(communityField).fieldName("(Ordens-) Gemeinschaft").notEmpty(true).validateOnChange();
		locationFieldValidator = TextValidator.create(locationField).fieldName("Ort").notEmpty(true).validateOnChange();
		fromDateValidator = DateValidator.create(fromDatePicker).fieldName("Von-Datum").notEmpty(true).validateOnChange();
		toDateValidator = DateValidator.create(toDatePicker).fieldName("Bis-Datum").notEmpty(true).after(fromDatePicker).relationFieldName("Von-Datum").validateOnChange();

		toDatePicker.setConverter(new DatePickerFormatter());
		fromDatePicker.setConverter(new DatePickerFormatter());
	}

	public void setAll(ExperienceAbroad experienceAbroad) {
		communityField.setText(experienceAbroad.getCommunity());
		detailsTextArea.setText(experienceAbroad.getDetails());
		locationField.setText(experienceAbroad.getLocation());
		fromDatePicker.setValue(experienceAbroad.getFromDate());
		toDatePicker.setValue(experienceAbroad.getToDate());
	}

	public ExperienceAbroad getAll(ExperienceAbroad experienceAbroad) {
		if (experienceAbroad == null) experienceAbroad = new ExperienceAbroad();
		experienceAbroad.setCommunity(communityField.getText());
		experienceAbroad.setDetails(detailsTextArea.getText());
		experienceAbroad.setLocation(locationField.getText());
		experienceAbroad.setFromDate(fromDatePicker.getValue());
		experienceAbroad.setToDate(toDatePicker.getValue());
		return experienceAbroad;
	}

	public void setReadonly(boolean readonly) {
		communityField.setDisable(readonly);
		detailsTextArea.setDisable(readonly);
		locationField.setDisable(readonly);
		fromDatePicker.setDisable(readonly);
		toDatePicker.setDisable(readonly);
	}

	public boolean isValid() {
		boolean communityValid = communityFieldValidator.validate();
		boolean toDateValid = toDateValidator.validate();
		boolean locationValid = locationFieldValidator.validate();
		boolean fromDateValid = fromDateValidator.validate();

		return communityValid && toDateValid && locationValid && fromDateValid;
	}
}

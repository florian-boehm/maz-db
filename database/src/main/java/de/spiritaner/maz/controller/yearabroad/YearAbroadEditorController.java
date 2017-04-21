package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.model.EPNumber;
import de.spiritaner.maz.model.YearAbroad;
import de.spiritaner.maz.util.factory.DatePickerFormatter;
import de.spiritaner.maz.util.factory.EPNumberCell;
import de.spiritaner.maz.util.validator.ComboBoxValidator;
import de.spiritaner.maz.util.validator.DateValidator;
import de.spiritaner.maz.util.validator.EPNumberValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.controlsfx.control.ToggleSwitch;
import tornadofx.control.DateTimePicker;

import java.net.URL;
import java.util.ResourceBundle;

public class YearAbroadEditorController implements Initializable {

	@FXML
	private TextField jobDescriptionField;
	@FXML
	private TextArea detailsArea;
	@FXML
	private DatePicker departureDatePicker;
	@FXML
	private DatePicker arrivalDatePicker;
	@FXML
	private DatePicker abortionDatePicker;
	@FXML
	private TextArea abortionReasonArea;
	@FXML
	private DateTimePicker missionDateTimePicker;
	@FXML
	private ToggleSwitch weltwaertsPromotedToggleSwitch;
	@FXML
	private ComboBox<EPNumber> epNumberComboBox;

	private DateValidator arrivalDateValidator;
	private DateValidator departureDateValidator;
	private EPNumberValidator epNumberValidator;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		arrivalDateValidator = DateValidator.create(arrivalDatePicker).fieldName("Rückreisedatum").relationFieldName("Abreisedatum").after(departureDatePicker).notEmpty(true).validateOnChange();
		departureDateValidator = DateValidator.create(departureDatePicker).fieldName("Abreisedatum").notEmpty(true).validateOnChange();
		epNumberValidator = new EPNumberValidator(epNumberComboBox).fieldName("EP-Nummer").isSelected(true).validateOnChange();

		arrivalDatePicker.setConverter(new DatePickerFormatter());
		abortionDatePicker.setConverter(new DatePickerFormatter());
		departureDatePicker.setConverter(new DatePickerFormatter());
		missionDateTimePicker.setConverter(new DatePickerFormatter());

		epNumberComboBox.setCellFactory(param -> EPNumberCell.epNumberListCell());
		epNumberComboBox.setButtonCell(EPNumberCell.epNumberListCell());
	}

	public void setAll(YearAbroad yearAbroad) {
		jobDescriptionField.setText(yearAbroad.getJobDescription());
		detailsArea.setText(yearAbroad.getDetails());
		departureDatePicker.setValue(yearAbroad.getDepartureDate());
		arrivalDatePicker.setValue(yearAbroad.getArrivalDate());
		abortionDatePicker.setValue(yearAbroad.getAbortionDate());
		abortionReasonArea.setText(yearAbroad.getAbortionReason());
		missionDateTimePicker.setValue(yearAbroad.getMissionDate());
		weltwaertsPromotedToggleSwitch.setSelected(yearAbroad.isWeltwaertsPromoted());
		epNumberComboBox.setValue(yearAbroad.getEpNumber());
	}

	public YearAbroad getAll(YearAbroad yearAbroad) {
		if(yearAbroad == null) yearAbroad = new YearAbroad();
		yearAbroad.setJobDescription(jobDescriptionField.getText());
		yearAbroad.setDetails(detailsArea.getText());
		yearAbroad.setDepartureDate(departureDatePicker.getValue());
		yearAbroad.setArrivalDate(arrivalDatePicker.getValue());
		yearAbroad.setAbortionDate(abortionDatePicker.getValue());
		yearAbroad.setAbortionReason(abortionReasonArea.getText());
		yearAbroad.setMissionDate(missionDateTimePicker.getValue());
		yearAbroad.setWeltwaertsPromoted(weltwaertsPromotedToggleSwitch.isSelected());
		yearAbroad.setEpNumber(epNumberComboBox.getValue());
		return yearAbroad;
	}

	public void setReadonly(boolean readonly) {
		jobDescriptionField.setDisable(readonly);
		detailsArea.setDisable(readonly);
		departureDatePicker.setDisable(readonly);
		arrivalDatePicker.setDisable(readonly);
		abortionDatePicker.setDisable(readonly);
		abortionDatePicker.setDisable(readonly);
		abortionReasonArea.setDisable(readonly);
		missionDateTimePicker.setDisable(readonly);
		weltwaertsPromotedToggleSwitch.setDisable(readonly);
		epNumberComboBox.setDisable(readonly);
	}

	public boolean isValid() {
		boolean departureDateValid = departureDateValidator.validate();
		boolean arrivalDateValid = arrivalDateValidator.validate();
		// TODO EPNumber validator is crying about a "not set epnumber" but it is set!
		boolean epNumberValid = epNumberValidator.validate();

		return departureDateValid && arrivalDateValid && epNumberValid;
	}

	public DatePicker getDepartureDatePicker() {
		return departureDatePicker;
	}

	public DatePicker getArrivalDatePicker() {
		return arrivalDatePicker;
	}

	public ComboBox<EPNumber> getEpNumberComboBox() {
		return epNumberComboBox;
	}
}

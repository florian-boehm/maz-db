package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.model.EPNumber;
import de.spiritaner.maz.model.YearAbroad;
import de.spiritaner.maz.util.validator.DateValidation;
import de.spiritaner.maz.util.validator.Selected;
import de.spiritaner.maz.util.validator.TextValidation;
import de.spiritaner.maz.view.component.*;
import de.spiritaner.maz.view.renderer.EPNumberCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.net.URL;
import java.util.ResourceBundle;

public class YearAbroadEditorController extends EditorController {

	public ObjectProperty<YearAbroad> yearAbroad = new SimpleObjectProperty<>();

	public BindableTextField wwMonthsField;
	public BindableTextField jobDescriptionField;
	public BindableTextArea detailsArea;
	public BindableDatePicker departureDatePicker;
	public BindableDatePicker arrivalDatePicker;
	public BindableDatePicker abortionDatePicker;
	public BindableTextArea abortionReasonArea;
	public BindableDatePicker missionDateTimePicker;
	public BindableToggleSwitch weltwaertsPromotedToggleSwitch;
	public SimpleBindableComboBox<EPNumber> epNumberComboBox;

	private DateValidation arrivalDateValidator;
	private DateValidation departureDateValidator;
	private Selected epNumberValidator;
	private TextValidation wwMonthsValidator;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		autoBinder.register(this);
		yearAbroad.addListener((observable, oldValue, newValue) -> autoBinder.rebindAll());

		arrivalDateValidator = DateValidation.create(arrivalDatePicker,"Rückreisedatum").relationFieldName("Abreisedatum").after(departureDatePicker).notEmpty(true).validateOnChange();
		departureDateValidator = DateValidation.create(departureDatePicker, "Abreisedatum").notEmpty(true).validateOnChange();
		epNumberValidator = new Selected(epNumberComboBox, "EP-Nummer");
		wwMonthsValidator = TextValidation.create(wwMonthsField, "Anzahl Monate (ww)").notEmpty(true).justNumbers().validateOnChange();

		wwMonthsField.setDisable(true);

		weltwaertsPromotedToggleSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
			wwMonthsField.setText((newValue) ? "12" : "0");
			wwMonthsField.setDisable(!newValue);
		});

		epNumberComboBox.setCellFactory(param -> EPNumberCell.epNumberListCell());
		epNumberComboBox.setButtonCell(EPNumberCell.epNumberListCell());
	}

	@Override
	public boolean isValid() {
		boolean departureDateValid = departureDateValidator.isValid();
		boolean arrivalDateValid = arrivalDateValidator.isValid();
		boolean epNumberValid = epNumberValidator.isValid();
		boolean wwMonthsValid = (wwMonthsField.isDisable()) || wwMonthsValidator.isValid();

		return departureDateValid && arrivalDateValid && epNumberValid && wwMonthsValid;
	}
}

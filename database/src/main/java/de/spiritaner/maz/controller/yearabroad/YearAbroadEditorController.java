package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.model.EPNumber;
import de.spiritaner.maz.model.YearAbroad;
import de.spiritaner.maz.util.validator.DateValidator;
import de.spiritaner.maz.util.validator.EPNumberValidator;
import de.spiritaner.maz.util.validator.TextValidator;
import de.spiritaner.maz.view.binding.AutoBinder;
import de.spiritaner.maz.view.component.*;
import de.spiritaner.maz.view.renderer.DatePickerFormatter;
import de.spiritaner.maz.view.renderer.EPNumberCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.controlsfx.control.ToggleSwitch;
import tornadofx.control.DateTimePicker;

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

	private DateValidator arrivalDateValidator;
	private DateValidator departureDateValidator;
	private EPNumberValidator epNumberValidator;
	private TextValidator wwMonthsValidator;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		AutoBinder ab = new AutoBinder(this);
		yearAbroad.addListener((observable, oldValue, newValue) -> ab.rebindAll());

		arrivalDateValidator = DateValidator.create(arrivalDatePicker).fieldName("Rückreisedatum").relationFieldName("Abreisedatum").after(departureDatePicker).notEmpty(true).validateOnChange();
		departureDateValidator = DateValidator.create(departureDatePicker).fieldName("Abreisedatum").notEmpty(true).validateOnChange();
		epNumberValidator = new EPNumberValidator(epNumberComboBox).fieldName("EP-Nummer").isSelected(true).validateOnChange();
		wwMonthsValidator = TextValidator.create(wwMonthsField).fieldName("Anzahl Monate (ww)").notEmpty(true).justNumbers().validateOnChange();

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
		boolean departureDateValid = departureDateValidator.validate();
		boolean arrivalDateValid = arrivalDateValidator.validate();
		boolean epNumberValid = epNumberValidator.validate();
		boolean wwMonthsValid = (wwMonthsField.isDisable()) || wwMonthsValidator.validate();

		return departureDateValid && arrivalDateValid && epNumberValid && wwMonthsValid;
	}
}

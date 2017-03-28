package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.model.YearAbroad;
import de.spiritaner.maz.util.validator.TextValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}

	public void setAll(YearAbroad yearAbroad) {
		jobDescriptionField.setText(yearAbroad.getJobDescription());
		detailsArea.setText(yearAbroad.getDetails());
		departureDatePicker.setValue(yearAbroad.getDepartureDate());
		arrivalDatePicker.setValue(yearAbroad.getArrivalDate());
		abortionDatePicker.setValue(yearAbroad.getAbortionDate());
		abortionReasonArea.setText(yearAbroad.getAbortionReason());
	}

	public YearAbroad getAll(YearAbroad yearAbroad) {
		if(yearAbroad == null) yearAbroad = new YearAbroad();
		yearAbroad.setJobDescription(jobDescriptionField.getText());
		yearAbroad.setDetails(detailsArea.getText());
		yearAbroad.setDepartureDate(departureDatePicker.getValue());
		yearAbroad.setArrivalDate(arrivalDatePicker.getValue());
		yearAbroad.setAbortionDate(abortionDatePicker.getValue());
		yearAbroad.setAbortionReason(abortionReasonArea.getText());
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
	}

	public boolean isValid() {
		return true;
	}
}

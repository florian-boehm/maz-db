package de.spiritaner.maz.controller.experienceabroad;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.model.ExperienceAbroad;
import de.spiritaner.maz.util.validator.DateValidator;
import de.spiritaner.maz.util.validator.TextValidator;
import de.spiritaner.maz.view.binding.AutoBinder;
import de.spiritaner.maz.view.binding.BindableProperty;
import de.spiritaner.maz.view.component.BindableDatePicker;
import de.spiritaner.maz.view.component.BindableTextArea;
import de.spiritaner.maz.view.component.BindableTextField;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class ExperienceAbroadEditorController extends EditorController {

	final static Logger logger = Logger.getLogger(ExperienceAbroadEditorController.class);

	@BindableProperty
	public ObjectProperty<ExperienceAbroad> experienceAbroad = new SimpleObjectProperty<>();

	public BindableTextField communityField;
	public BindableTextField locationField;
	public BindableTextArea detailsTextArea;
	public BindableDatePicker fromDatePicker;
	public BindableDatePicker toDatePicker;

	private TextValidator communityFieldValidator;
	private TextValidator locationFieldValidator;
	private DateValidator fromDateValidator;
	private DateValidator toDateValidator;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		AutoBinder ab = new AutoBinder(this);

		experienceAbroad.addListener((observable, oldValue, newValue) -> ab.rebindAll());

		// TODO Extract strings
		communityFieldValidator = TextValidator.create(communityField).fieldName("(Ordens-) Gemeinschaft").notEmpty(true).validateOnChange();
		locationFieldValidator = TextValidator.create(locationField).fieldName("Ort").notEmpty(true).validateOnChange();
		fromDateValidator = DateValidator.create(fromDatePicker).fieldName("Von-Datum").notEmpty(true).validateOnChange();
		toDateValidator = DateValidator.create(toDatePicker).fieldName("Bis-Datum").notEmpty(true).after(fromDatePicker).relationFieldName("Von-Datum").validateOnChange();
	}

	@Override
	public boolean isValid() {
		boolean communityValid = communityFieldValidator.validate();
		boolean toDateValid = toDateValidator.validate();
		boolean locationValid = locationFieldValidator.validate();
		boolean fromDateValid = fromDateValidator.validate();

		return communityValid && toDateValid && locationValid && fromDateValid;
	}
}

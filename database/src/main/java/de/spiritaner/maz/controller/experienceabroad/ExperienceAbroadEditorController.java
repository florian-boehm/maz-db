package de.spiritaner.maz.controller.experienceabroad;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.model.ExperienceAbroad;
import de.spiritaner.maz.util.validator.*;
import de.spiritaner.maz.view.binding.BindableProperty;
import de.spiritaner.maz.view.component.BindableDatePicker;
import de.spiritaner.maz.view.component.BindableTextArea;
import de.spiritaner.maz.view.component.BindableTextField;
import de.spiritaner.maz.view.renderer.DatePickerFormatter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.net.URL;
import java.util.ResourceBundle;

public class ExperienceAbroadEditorController extends EditorController {

	@BindableProperty
	public ObjectProperty<ExperienceAbroad> experienceAbroad = new SimpleObjectProperty<>();

	public BindableTextField communityField;
	public BindableTextField locationField;
	public BindableTextArea detailsTextArea;
	public BindableDatePicker fromDatePicker;
	public BindableDatePicker toDatePicker;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Bind all bindable fields to the bindable property
		autoBinder.register(this);
		experienceAbroad.addListener((observable, oldValue, newValue) -> super.autoBinder.rebindAll());

		// Change the validator visitor to PopOver and add Validations as well as change listeners
		autoValidator.visitor = new PopOverVisitor();
		autoValidator.add(new NotEmpty(communityField, guiText.getString("community")));
		autoValidator.add(new NotEmpty(locationField, guiText.getString("location")));
		autoValidator.add(new NotEmpty(fromDatePicker, guiText.getString("from_date")));
		autoValidator.add(new NotEmpty(toDatePicker, guiText.getString("to_date")));
		autoValidator.add(new After(toDatePicker, guiText.getString("to_date"), fromDatePicker, guiText.getString("from_date"), false));

		// autoValidator.nodeValidations.add(new After(new NotEmpty(toDatePicker, guiText.getString("to_date")), fromDatePicker, guiText.getString("from_date")));
		//autoValidator.add(toDatePicker, DateValidation.create(toDatePicker, guiText.getString("to_date")).notEmpty(true).after(fromDatePicker).relationFieldName(guiText.getString("from_date")));

		// Custom format of some fields
		toDatePicker.setConverter(new DatePickerFormatter());
		fromDatePicker.setConverter(new DatePickerFormatter());
	}
}

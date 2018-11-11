package de.spiritaner.maz.util.validator;


import de.spiritaner.maz.view.component.BindableDatePicker;
import de.spiritaner.maz.view.component.BindableTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ResourceBundle;

public class NotEmpty extends Validation {

	protected final ResourceBundle guiText = ResourceBundle.getBundle("lang.gui");
	BooleanProperty valid = new SimpleBooleanProperty(false);

	public NotEmpty(final BindableTextField textField, final String fieldName) {
		super(textField, fieldName);
		// TODO Trim is necessary but does not really work ... Why?
		//valid.bind(textField.textProperty().isNotEmpty());
		//valid.bind(Bindings.createBooleanBinding(() -> ((BindableTextField) this.node).getText().trim().isEmpty(),
		//		((BindableTextField) this.node).textProperty()));
		valid.bind(Bindings.createBooleanBinding(() -> {
			if(textField.getText() == null)
				return false;
			else
				return !textField.getText().trim().isEmpty();
			}, textField.textProperty()));
	}

	public NotEmpty(final BindableDatePicker datePicker, final String fieldName) {
		super(datePicker, fieldName);
		valid.bind(datePicker.valueProperty().isNotNull());
	}

	public boolean isValid() {
		boolean result = valid.get();

		msg.set((result) ? "" : fieldName + " " + guiText.getString("must_not_be_empty"));

		return valid.get();
	}
}

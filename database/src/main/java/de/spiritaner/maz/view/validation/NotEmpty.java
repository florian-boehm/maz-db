package de.spiritaner.maz.view.validation;


import javafx.beans.property.StringProperty;
import javafx.scene.control.TextInputControl;

public class NotEmpty implements Validator {

	StringProperty validatedProperty;

	public NotEmpty(TextInputControl node) {
		validatedProperty.bind(node.textProperty());
	}

	public boolean validate() {
		return !validatedProperty.get().trim().isEmpty();
	}
}

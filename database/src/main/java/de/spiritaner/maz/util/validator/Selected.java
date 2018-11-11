package de.spiritaner.maz.util.validator;


import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ComboBox;

import java.util.ResourceBundle;

public class Selected extends Validation {

	protected final ResourceBundle guiText = ResourceBundle.getBundle("lang.gui");
	private BooleanProperty valid = new SimpleBooleanProperty();

	public Selected(ComboBox comboBox, String fieldName) {
		super(comboBox, fieldName);

		/*valid.bind(new BooleanBinding() {
			@Override
			protected boolean computeValue() {
				return comboBox.getSelectionModel().getSelectedIndex() >= 0;
			}
		});*/
		valid.bind(Bindings.createBooleanBinding(() -> comboBox.getSelectionModel().getSelectedIndex() >= 0, comboBox.valueProperty()));


		//comboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateAndShow());
	}

	public boolean isValid() {
		boolean result = valid.get();

		msg.set((result) ? "" : fieldName + " " + guiText.getString("must_be_selected"));

		return valid.get();
	}
}

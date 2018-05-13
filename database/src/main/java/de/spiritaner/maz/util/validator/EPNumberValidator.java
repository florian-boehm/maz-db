package de.spiritaner.maz.util.validator;

import de.spiritaner.maz.model.EPNumber;
import de.spiritaner.maz.view.validation.Validator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

public class EPNumberValidator implements Validator {

	private PopOver popOver;
	private VBox vbox;
	private ComboBox<EPNumber> comboBox;
	private Boolean selected;
	private String fieldName;

	public EPNumberValidator(ComboBox<EPNumber> comboBox) {
		this.comboBox = comboBox;

		vbox = new VBox();
		vbox.setAlignment(Pos.CENTER_LEFT);
		vbox.setPadding(new Insets(2));

		popOver = new PopOver();
		popOver.setAutoHide(false);
		popOver.setDetachable(false);
		popOver.setContentNode(vbox);

		// Set default value
		selected = Boolean.FALSE;
	}

	public EPNumberValidator validateOnChange() {
		comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			validate(oldValue, newValue);
		});

		return this;
	}

	public EPNumberValidator fieldName(String fieldName) {
		this.fieldName = fieldName;
		return this;
	}

	public EPNumberValidator isSelected(boolean selected) {
		this.selected = selected;
		return this;
	}

	public void addMsg(String message) {
		Label label = new Label(message);
		label.setPadding(new Insets(2));
		label.setStyle("-fx-text-fill: #B80024; -fx-font-weight: bold");
		vbox.getChildren().add(label);
	}

	public boolean validate(EPNumber oldValue, EPNumber newValue) {
		boolean result = true;

		vbox.getChildren().clear();

		if (comboBox == null) {
			return false;
		}

		// Check if value is selected
		if (selected && comboBox.getSelectionModel().getSelectedIndex() < 0) {
			addMsg(fieldName + " muss ausgewählt werden!");
			result = false;
		}

		// Hide or show the messages
		if (result) {
			popOver.hide();
		} else {
			try {
				if (!popOver.isShowing())
					popOver.setAutoHide(true);
					popOver.show(comboBox);
			} catch (NullPointerException e) {
				// TODO find a way to suppress the nullpointer exception that gets thrown when "setAll" sets the values and the text changes
			}
		}

		return result;
	}

	public boolean validate() {
		return validate(comboBox.getValue(), comboBox.getValue());
	}
}

package de.spiritaner.maz.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Florian on 8/11/2016.
 */
public class TextValidator {

	private PopOver popOver;
//	private Label msgLabel;
	private VBox vbox;
	private TextField textField;

	private Integer minLenght;
	private Integer maxLength;
	private Boolean notEmpty;
	private Boolean justText;
	private ArrayList<String> removeAll;
	private TextField originalField;

	private String fieldName;

	private TextValidator() {
		vbox = new VBox();
		vbox.setAlignment(Pos.CENTER_LEFT);
		vbox.setPadding(new Insets(2));
//		msgLabel = new Label("");
//		msgLabel.setStyle("-fx-text-fill: #B80024; -fx-font-weight: bold");
//		msgLabel.setPadding(new Insets(4));

		popOver = new PopOver();
		popOver.setAutoHide(false);
		popOver.setDetachable(false);
		popOver.setContentNode(vbox);
	}

	public static TextValidator create(TextField node) {
		TextValidator result = new TextValidator();
		result.textField = node;
		return result;
	}

	public void addMsg(String message) {
		Label label = new Label(message);
		label.setPadding(new Insets(2));
		label.setStyle("-fx-text-fill: #B80024; -fx-font-weight: bold");
		vbox.getChildren().add(label);
	}

	public TextValidator textChanged() {
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			validate(oldValue, newValue);
		});

		return this;
	}

	public TextValidator min(Integer minLenght) {
		this.minLenght = minLenght;
		return this;
	}

	public TextValidator fieldName(String fieldName) {
		this.fieldName = fieldName;
		return this;
	}

	public TextValidator notEmpty(Boolean notEmpty) {
		this.notEmpty = notEmpty;
		return this;
	}

	public TextValidator justText() {
		this.justText = true;
		return this;
	}

	public TextValidator removeAll(String... remove) {
		this.removeAll = new ArrayList<String>(Arrays.asList(remove));
		return this;
	}

	public TextValidator max(Integer maxLength) {
		this.maxLength = maxLength;
		return this;
	}

	public TextValidator equals(TextField originalField) {
		this.originalField = originalField;
		return this;
	}

	public boolean validate(String oldValue, String newValue) {
		boolean result = true;

        if(textField == null) {
            return false;
        }

        // Set to empty string if it is null to avoid null pointer exceptions
		final String newText = (newValue == null) ? "" : newValue;
		final String oldText = (oldValue == null) ? "" : oldValue;

		if(removeAll != null && !oldText.equals(newText)) {
			removeAll.forEach(needle -> {
				textField.setText(newText.replace(needle, ""));
			});
		}

		vbox.getChildren().clear();

		if(justText != null && justText == true) {
			String after = newText.replaceAll("[^a-zA-Z0-9-_]","");
			if(!after.equals(newText)) {
				result = false;
				addMsg(fieldName+" darf keine Sonderzeichen enthalten!");
			}

//			textField.setText(textField.getText().replaceAll("[^a-zA-Z0-9-_]",""));
		}

		// Check if the text is shorter than allowed
		if(minLenght != null && newText.length() < minLenght) {
			addMsg(fieldName+" muss mindestens "+minLenght+" Zeichen lang sein!");
			result = false;
		}

		// Check if the text is longer than allowed
		if(maxLength != null && newText.length() > maxLength) {
			addMsg(fieldName + " darf nicht mehr als "+maxLength+" Zeichen lang sein!");
			result = false;
		}

		// Check if the value is null
		if(notEmpty != null && newText.trim().isEmpty()) {
			addMsg(fieldName + " darf nicht leer sein!");
			result = false;
		}

		// Check if the value is null
		if(originalField != null && !newText.equals(originalField.getText())) {
			addMsg(fieldName + " stimmt nicht überein!");
			result = false;
		}

		// Hide or show the messages
		if(result) {
			popOver.hide();
		} else {
            try {
                if(!popOver.isShowing())
                    popOver.show(textField);
            } catch (NullPointerException e) {
                // TODO find a way to suppress the nullpointer exception that gets thrown when "setAll" sets the values and the text changes
            }
		}

		return result;
	}

	public boolean validate() {
		return validate(textField.getText(),textField.getText());
	}
}

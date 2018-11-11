package de.spiritaner.maz.util.validator;

import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Florian on 8/11/2016.
 */
public class TextValidation extends Validation {

	private TextField textField;

	private Integer minLenght;
	private Integer maxLength;
	private Boolean notEmpty;
	private Boolean justText;
	private Boolean justNumbers;
	private ArrayList<String> removeAll;
	private TextField originalField;

	public TextValidation(Node node, String fieldName) {
		super(node, fieldName);
	}

	@Override
	public String toString() {
		return "";
	}

	public static TextValidation create(TextField node, String fieldName) {
		TextValidation result = new TextValidation(node, fieldName);
		result.textField = node;
		return result;
	}

	public TextValidation validateOnChange() {
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			validate(oldValue, newValue);
		});

		return this;
	}

	public TextValidation min(Integer minLenght) {
		this.minLenght = minLenght;
		return this;
	}

	public TextValidation fieldName(String fieldName) {
		this.fieldName = fieldName;
		return this;
	}

	public TextValidation notEmpty(Boolean notEmpty) {
		this.notEmpty = notEmpty;
		return this;
	}

	public TextValidation justText() {
		this.justText = true;
		return this;
	}

	public TextValidation removeAll(String... remove) {
		this.removeAll = new ArrayList<String>(Arrays.asList(remove));
		return this;
	}

	public TextValidation max(Integer maxLength) {
		this.maxLength = maxLength;
		return this;
	}

	public TextValidation equals(TextField originalField) {
		this.originalField = originalField;
		return this;
	}

	public TextValidation justNumbers() {
		this.justNumbers = true;
		return this;
	}

	public boolean validate(String oldValue, String newValue) {
		boolean result = true;

		if (textField == null) {
			return false;
		}

		// Set to empty string if it is null to avoid null pointer exceptions
		final String newText = (newValue == null) ? "" : newValue;
		final String oldText = (oldValue == null) ? "" : oldValue;

		if (removeAll != null && !oldText.equals(newText)) {
			removeAll.forEach(needle -> {
				textField.setText(newText.replace(needle, ""));
			});
		}

		if (justText != null && justText) {
			String after = newText.replaceAll("[^a-zA-Z0-9-_]", "");
			if (!after.equals(newText)) {
				result = false;
				msg.set(fieldName + " darf keine Sonderzeichen enthalten!");
			}

//			textField.setText(textField.getText().replaceAll("[^a-zA-Z0-9-_]",""));
		}

		if (justNumbers != null && justNumbers) {
			String after = newText.replaceAll("[^0-9]", "");
			if (!after.equals(newText)) {
				result = false;
				msg.set(fieldName + "darf nur Zahlen enthalten!");
			}
		}

		// Check if the text is shorter than allowed
		if (minLenght != null && newText.length() < minLenght) {
			msg.set(fieldName + " muss mindestens " + minLenght + " Zeichen lang sein!");
			result = false;
		}

		// Check if the text is longer than allowed
		if (maxLength != null && newText.length() > maxLength) {
			msg.set(fieldName + " darf nicht mehr als " + maxLength + " Zeichen lang sein!");
			result = false;
		}

		// Check if the value is null
		if (notEmpty != null && newText.trim().isEmpty()) {
			msg.set(fieldName + " darf nicht leer sein!");
			result = false;
		}

		// Check if the value is null
		if (originalField != null && !newText.equals(originalField.getText())) {
			msg.set(fieldName + " stimmt nicht überein!");
			result = false;
		}

		return result;
	}

	public boolean isValid() {
		return validate(textField.getText(), textField.getText());
	}
}

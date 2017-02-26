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
public class DateValidator {

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

	private DateValidator() {
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

	public static DateValidator create(TextField node) {
		DateValidator result = new DateValidator();
		result.textField = node;
		return result;
	}

	public void addMsg(String message) {
		Label label = new Label(message);
		label.setPadding(new Insets(2));
		label.setStyle("-fx-text-fill: #B80024; -fx-font-weight: bold");
		vbox.getChildren().add(label);
	}

	public DateValidator textChanged() {
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			validate(oldValue, newValue);
		});

		return this;
	}

	public DateValidator min(Integer minLenght) {
		this.minLenght = minLenght;
		return this;
	}

	public DateValidator fieldName(String fieldName) {
		this.fieldName = fieldName;
		return this;
	}

	public DateValidator notEmpty(Boolean notEmpty) {
		this.notEmpty = notEmpty;
		return this;
	}

	public DateValidator justText() {
		this.justText = true;
		return this;
	}

	public DateValidator removeAll(String... remove) {
		this.removeAll = new ArrayList<String>(Arrays.asList(remove));
		return this;
	}

	public DateValidator max(Integer maxLength) {
		this.maxLength = maxLength;
		return this;
	}

	public DateValidator equals(TextField originalField) {
		this.originalField = originalField;
		return this;
	}

	public boolean validate(String oldValue, String newValue) {
		boolean result = true;

		if(removeAll != null && !oldValue.equals(newValue)) {
			removeAll.forEach(needle -> {
				textField.setText(textField.getText().replace(needle, ""));
			});
		}

		vbox.getChildren().clear();

		if(textField == null) {
			System.out.println("SOMETHING IS WRONG HERE");
			return false;
		}

		if(justText != null && justText == true) {
			String after = textField.getText().replaceAll("[^a-zA-Z0-9-_]","");
			if(!after.equals(textField.getText())) {
				result = false;
				addMsg(fieldName+" darf keine Sonderzeichen enthalten!");
			}

//			textField.setText(textField.getText().replaceAll("[^a-zA-Z0-9-_]",""));
		}

		// Check if the text is shorter than allowed
		if(minLenght != null && textField.getText().length() < minLenght) {
			addMsg(fieldName+" muss mindestens "+minLenght+" Zeichen lang sein!");
			result = false;
		}

		// Check if the text is longer than allowed
		if(maxLength != null && textField.getText().length() > maxLength) {
			addMsg(fieldName + " darf nicht mehr als "+maxLength+" Zeichen lang sein!");
			result = false;
		}

		// Check if the value is null
		if(notEmpty != null && textField.getText().trim().isEmpty()) {
			addMsg(fieldName + " darf nicht leer sein!");
			result = false;
		}

		// Check if the value is null
		if(originalField != null && !textField.getText().equals(originalField.getText())) {
			addMsg(fieldName + " stimmt nicht überein!");
			result = false;
		}

		// Hide or show the messages
		if(result == true)
			popOver.hide();
		else
			if(!popOver.isShowing())
				popOver.show(textField);

		return result;
	}

	public boolean validate() {
		return validate("","");
	}
}

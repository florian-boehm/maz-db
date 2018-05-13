package de.spiritaner.maz.util.validator;

import de.spiritaner.maz.view.validation.Validator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

import java.time.LocalDate;

public class DateValidator implements Validator {

	private PopOver popOver;
	private VBox vbox;
	private DatePicker datePicker;

	private Boolean notEmpty;
	private Boolean future;

	private DatePicker relationField;
	private String relationFieldName;
	private Boolean beforeRelation;
	private Boolean sameRelation;

	private String fieldName;

	private DateValidator() {
		vbox = new VBox();
		vbox.setAlignment(Pos.CENTER_LEFT);
		vbox.setPadding(new Insets(2));

		popOver = new PopOver();
		popOver.setAutoHide(false);
		popOver.setDetachable(false);
		popOver.setContentNode(vbox);
	}

	public static DateValidator create(DatePicker node) {
		DateValidator result = new DateValidator();
		result.datePicker = node;
		return result;
	}

	public void addMsg(String message) {
		Label label = new Label(message);
		label.setPadding(new Insets(2));
		label.setStyle("-fx-text-fill: #B80024; -fx-font-weight: bold");
		vbox.getChildren().add(label);
	}

	public DateValidator validateOnChange() {
		datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			validate(oldValue, newValue);
		});

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

	public DateValidator future() {
		this.future = Boolean.TRUE;
		return this;
	}

	public DateValidator past() {
		this.future = Boolean.FALSE;
		return this;
	}

	public DateValidator relationFieldName(String relationFieldName) {
		this.relationFieldName = relationFieldName;
		return this;
	}

	public DateValidator before(DatePicker relationField, Boolean sameRelation) {
		this.sameRelation = sameRelation;
		this.relationField = relationField;
		this.beforeRelation = Boolean.TRUE;
		return this;
	}

	public DateValidator before(DatePicker relationField) {
		return before(relationField,Boolean.TRUE);
	}

	public DateValidator after(DatePicker relationField, Boolean sameRelation) {
		this.sameRelation = sameRelation;
		this.relationField = relationField;
		this.beforeRelation = Boolean.FALSE;
		return this;
	}

	public DateValidator after(DatePicker relationField) {
		return after(relationField, Boolean.TRUE);
	}

	public boolean validate(LocalDate oldValue, LocalDate newValue) {
		boolean result = true;

		vbox.getChildren().clear();

		// Check if the value is null
		if (notEmpty != null && newValue == null) {
			addMsg(fieldName + " darf nicht leer sein!");
			result = false;
		} else {
			// Check if the date is a future date
			if (future != null && future && newValue.compareTo(LocalDate.now()) < 0) {
				addMsg(fieldName + " muss in der Zukunft liegen!");
				result = false;
			} else if (future != null && !future && newValue.compareTo(LocalDate.now()) > 0) {
				addMsg(fieldName + " muss in der Vergangenheit liegen!");
				result = false;
			}
		}

		if (relationField != null && relationField.getValue() != null && newValue != null) {
			if(beforeRelation && !sameRelation && (newValue.isAfter(relationField.getValue()) || newValue.isEqual(relationField.getValue()))) {
				addMsg(fieldName + " muss vor " + relationFieldName + " sein!");
				result = false;
			} else if(beforeRelation && sameRelation && newValue.isAfter(relationField.getValue())) {
				addMsg(fieldName + " muss vor oder gleich " + relationFieldName + " sein!");
				result = false;
			} else if(!beforeRelation && !sameRelation && (newValue.isBefore(relationField.getValue()) || newValue.isEqual(relationField.getValue()))) {
				addMsg(fieldName + " muss nach " + relationFieldName + " sein!");
				result = false;
			} else if(!beforeRelation && sameRelation && newValue.isBefore(relationField.getValue())) {
				addMsg(fieldName + " muss vor oder gleich " + relationFieldName + " sein!");
				result = false;
			}
		}

		// Hide or show the messages
		if (result == true)
			popOver.hide();
		else if (!popOver.isShowing()) {
			popOver.setAutoHide(true);
			popOver.show(datePicker);
		}

		return result;
	}

	public boolean validate() {
		return validate(null, datePicker.getValue());
	}
}

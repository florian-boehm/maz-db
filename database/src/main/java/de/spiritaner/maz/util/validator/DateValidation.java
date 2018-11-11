package de.spiritaner.maz.util.validator;

import javafx.scene.Node;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;

public class DateValidation extends Validation {

	private DatePicker datePicker;

	private Boolean notEmpty;
	private Boolean future;

	private DatePicker relationField;
	private String relationFieldName;
	private Boolean beforeRelation;
	private Boolean sameRelation;

	public DateValidation(Node node, String fieldName) {
		super(node, fieldName);
	}

	@Override
	public String toString() {
		return "";
	}

	public static DateValidation create(DatePicker node, String fieldName) {
		DateValidation result = new DateValidation(node, fieldName);
		result.fieldName = fieldName;
		result.datePicker = node;
		return result;
	}

	public DateValidation validateOnChange() {
		datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			validate(oldValue, newValue);
		});

		return this;
	}

	public DateValidation fieldName(String fieldName) {
		this.fieldName = fieldName;
		return this;
	}

	public DateValidation notEmpty(Boolean notEmpty) {
		this.notEmpty = notEmpty;
		return this;
	}

	public DateValidation future() {
		this.future = Boolean.TRUE;
		return this;
	}

	public DateValidation past() {
		this.future = Boolean.FALSE;
		return this;
	}

	public DateValidation relationFieldName(String relationFieldName) {
		this.relationFieldName = relationFieldName;
		return this;
	}

	public DateValidation before(DatePicker relationField, Boolean sameRelation) {
		this.sameRelation = sameRelation;
		this.relationField = relationField;
		this.beforeRelation = Boolean.TRUE;
		return this;
	}

	public DateValidation before(DatePicker relationField) {
		return before(relationField,Boolean.TRUE);
	}

	public DateValidation after(DatePicker relationField, Boolean sameRelation) {
		this.sameRelation = sameRelation;
		this.relationField = relationField;
		this.beforeRelation = Boolean.FALSE;
		return this;
	}

	public DateValidation after(DatePicker relationField) {
		return after(relationField, Boolean.TRUE);
	}

	public boolean validate(LocalDate oldValue, LocalDate newValue) {
		boolean result = true;

		//vbox.getChildren().clear();

		// Check if the value is null
		if (notEmpty != null && newValue == null) {
			msg.set(fieldName + " darf nicht leer sein!");
			result = false;
		} else {
			// Check if the date is a future date
			if (future != null && future && newValue.compareTo(LocalDate.now()) < 0) {
				msg.set(fieldName + " muss in der Zukunft liegen!");
				result = false;
			} else if (future != null && !future && newValue.compareTo(LocalDate.now()) > 0) {
				msg.set(fieldName + " muss in der Vergangenheit liegen!");
				result = false;
			}
		}

		if (relationField != null && relationField.getValue() != null && newValue != null) {
			if(beforeRelation && !sameRelation && (newValue.isAfter(relationField.getValue()) || newValue.isEqual(relationField.getValue()))) {
				msg.set(fieldName + " muss vor " + relationFieldName + " sein!");
				result = false;
			} else if(beforeRelation && sameRelation && newValue.isAfter(relationField.getValue())) {
				msg.set(fieldName + " muss vor oder gleich " + relationFieldName + " sein!");
				result = false;
			} else if(!beforeRelation && !sameRelation && (newValue.isBefore(relationField.getValue()) || newValue.isEqual(relationField.getValue()))) {
				msg.set(fieldName + " muss nach " + relationFieldName + " sein!");
				result = false;
			} else if(!beforeRelation && sameRelation && newValue.isBefore(relationField.getValue())) {
				msg.set(fieldName + " muss vor oder gleich " + relationFieldName + " sein!");
				result = false;
			}
		}

		// Hide or show the messages
		/*if (result == true)
			popOver.hide();
		else if (!popOver.isShowing()) {
			popOver.setAutoHide(true);
			popOver.show(datePicker);
		}*/

		return result;
	}

	public boolean isValid() {
		return validate(null, datePicker.getValue());
	}
}

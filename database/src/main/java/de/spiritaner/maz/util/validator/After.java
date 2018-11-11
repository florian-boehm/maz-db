package de.spiritaner.maz.util.validator;

import de.spiritaner.maz.view.component.BindableDatePicker;

public class After extends Validation {

	BindableDatePicker fromDate;
	BindableDatePicker toDate;
	String toDateFieldName;
	String fromDateFieldName;
	boolean allowSame;

	public After(BindableDatePicker toDate, String fieldName, BindableDatePicker fromDate, String fromDateFieldName, boolean allowSame) {
		super(toDate, fieldName);
		this.fromDate = fromDate;
		this.fromDateFieldName = fromDateFieldName;
		this.toDate = toDate;
		this.toDateFieldName = fieldName;
		this.allowSame = allowSame;
	}

	@Override
	public boolean isValid() {
		boolean result;

		if(fromDate.getValue() == null || toDate.getValue() == null) {
			msg.set("");
			return true;
		}

		if(allowSame) {
			result = fromDate.getValue().isBefore(toDate.getValue()) || fromDate.getValue().isEqual(toDate.getValue());
		} else {
			result = fromDate.getValue().isBefore(toDate.getValue());
		}

		if(!result && allowSame) {
			msg.set(toDateFieldName + " muss nach " + fromDateFieldName + " sein!");
		} else if(!result && !allowSame) {
			msg.set(toDateFieldName + " muss nach oder gleich " + fromDateFieldName + " sein!");
		} else {
			msg.set("");
		}

		return result;
	}
}

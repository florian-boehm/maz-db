package de.spiritaner.maz.util.factories;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatePickerFormatter extends StringConverter<LocalDate> {

	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	@Override
	public String toString(LocalDate localDate) {
		if (localDate == null)
			return "";
		return dateTimeFormatter.format(localDate);
	}

	@Override
	public LocalDate fromString(String dateString) {
		if (dateString == null || dateString.trim().isEmpty()) {
			return null;
		}
		return LocalDate.parse(dateString, dateTimeFormatter);
	}
}

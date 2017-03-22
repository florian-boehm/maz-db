package de.spiritaner.maz.util.factories;

import de.spiritaner.maz.model.Person;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Created by Florian on 3/22/2017.
 */
public class DateAsStringListCell {

	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	private DateAsStringListCell() {
	}

	public static ListCell<LocalDate> localeDateListCell() {
		return new ListCell<LocalDate>() {
			@Override
			public void updateItem(final LocalDate item, final boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					setText(dateFormatter.format(item));
				}
			}
		};
	}

	public static <T> TableCell<T, LocalDate> localDateTableCell() {
		return new TableCell<T, LocalDate>() {
			@Override
			protected void updateItem(final LocalDate item, final boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					setText(dateFormatter.format(item));
				}
			}
		};
	}

	public static <T> TableCell<T, LocalDate> localDateTableCellYearsToNow() {
		return new TableCell<T, LocalDate>() {
			@Override
			protected void updateItem(final LocalDate item, final boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					final LocalDate today = LocalDate.now();
					final long years = item.until(today, ChronoUnit.YEARS);
					setText(years+" Jahre");
				}
			}
		};
	}
}

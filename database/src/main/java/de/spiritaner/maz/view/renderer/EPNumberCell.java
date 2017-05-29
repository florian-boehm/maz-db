package de.spiritaner.maz.view.renderer;

import de.spiritaner.maz.model.EPNumber;
import de.spiritaner.maz.model.YearAbroad;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;

public class EPNumberCell {
	public static ListCell<EPNumber> epNumberListCell() {
		return new ListCell<EPNumber>() {
			@Override
			protected void updateItem(final EPNumber item, final boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					setText(item.getNumber() + " - "+item.getDescription());
				}
			}
		};
	}

	public static TableCell<YearAbroad, EPNumber> epNumberTableCell() {
		return new TableCell<YearAbroad, EPNumber>() {
			@Override
			protected void updateItem(final EPNumber item, final boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					setText(item.getNumber() + " - " + item.getDescription());
				}
			}
		};
	}
}

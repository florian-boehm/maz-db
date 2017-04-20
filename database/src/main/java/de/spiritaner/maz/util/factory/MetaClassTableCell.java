package de.spiritaner.maz.util.factory;

import de.spiritaner.maz.model.meta.MetaClass;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;

public class MetaClassTableCell<S, T extends MetaClass> extends TableCell<S, T> {

	@Override
	public void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);

		if (item == null || empty) {
			setText(null);
			setStyle("");
		} else {
			setText(item.getDescription());
		}
	}
}

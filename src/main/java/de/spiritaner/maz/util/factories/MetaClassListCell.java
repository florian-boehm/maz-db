package de.spiritaner.maz.util.factories;

import de.spiritaner.maz.model.meta.MetaClass;
import javafx.scene.control.ListCell;

public class MetaClassListCell<T extends MetaClass> extends ListCell<T> {

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

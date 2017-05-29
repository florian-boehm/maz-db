package de.spiritaner.maz.view.renderer;

import javafx.scene.control.TableCell;

public class BooleanTableCell<S, T> extends TableCell<S, T> {

	@Override
	public  void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);

		if (item == null || empty || !(item instanceof Boolean)) {
			setText(null);
			setStyle("kA");
		} else {
			setText(((Boolean) item) ? "Ja" : "Nein");
		}
	}
}

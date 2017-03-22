package de.spiritaner.maz.util.envers;

import de.spiritaner.maz.model.Identifiable;
import javafx.scene.control.ListCell;

public class RevisionEntityListCell<T extends Identifiable> extends ListCell<RevisionEntity<T>> {

	@Override
	public void updateItem(RevisionEntity<T> item, boolean empty) {
		super.updateItem(item, empty);

		if (item == null || empty || item.getEntity() == null) {
			setText("Aktuell");
		} else {
			setText(item.getRevisionDate("HH:mm dd.MM.yy"));
		}
	}
}

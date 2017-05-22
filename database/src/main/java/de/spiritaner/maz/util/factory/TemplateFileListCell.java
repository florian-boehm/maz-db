package de.spiritaner.maz.util.factory;

import javafx.scene.control.ListCell;

import java.io.File;
import java.util.ResourceBundle;

public class TemplateFileListCell extends ListCell<File> {

	final ResourceBundle guiText = ResourceBundle.getBundle("lang.gui");

	@Override
	public void updateItem(File item, boolean empty) {
		super.updateItem(item, empty);

		if (item == null || empty) {
			setText(guiText.getString("none"));
		} else {
			String displayName = item.getName();
			displayName = displayName.replace("_"," ").replace(".docx","");
			setText(displayName);
		}
	}
}
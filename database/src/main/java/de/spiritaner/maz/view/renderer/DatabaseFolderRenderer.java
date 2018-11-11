package de.spiritaner.maz.view.renderer;

import de.spiritaner.maz.util.database.DatabaseFolder;
import javafx.scene.control.ListCell;

import java.util.ResourceBundle;

/**
 * @author Florian Böhm
 * @version 2017.05.25
 */
public class DatabaseFolderRenderer extends ListCell<DatabaseFolder> {

    final ResourceBundle guiText = ResourceBundle.getBundle("lang.gui");

    @Override
    public void updateItem(DatabaseFolder item, boolean empty) {
        super.updateItem(item, empty);
        setPrefHeight(40.0);

        if (item == null || empty) {
            setText(null);
            setStyle("");
        } else {
            setText(item.getDisplayName());
        }
    }
}
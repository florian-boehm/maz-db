package de.spiritaner.maz.view.renderer;

import de.spiritaner.maz.util.Settings;
import de.spiritaner.maz.util.database.DatabaseFolder;
import javafx.scene.control.ListCell;

import javax.xml.crypto.Data;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * @author Florian Schwab
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
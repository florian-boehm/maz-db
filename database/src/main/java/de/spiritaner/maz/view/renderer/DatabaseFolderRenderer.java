package de.spiritaner.maz.view.renderer;

import javafx.scene.control.ListCell;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * @author Florian Schwab
 * @version 2017.05.25
 */
public class DatabaseFolderRenderer extends ListCell<File> {

    final DateTimeFormatter fromFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    final DateTimeFormatter toFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    final ResourceBundle guiText = ResourceBundle.getBundle("lang.gui");

    @Override
    public void updateItem(File item, boolean empty) {
        super.updateItem(item, empty);
        setPrefHeight(40.0);

        if (item == null || empty) {
            setText(null);
            setStyle("");
            //setText(guiText.getString("none"));
        } else {
            String postFix = (new File(item, "db.lock").exists()) ? " (Lesezugriff)" : "";

            if (item.getName().equals("dbfiles")) {
                setText(guiText.getString("current")+postFix);
            } else if (item.getName().contains("-")) {
                String name = item.getName();
                String time = name.substring(name.indexOf("-") + 1);
                setText(guiText.getString("backup") + " (" + toFormatter.format(fromFormatter.parse(time)) + ")"+postFix);
            } else {
                setText(item.getName()+postFix);
            }
        }
    }
}
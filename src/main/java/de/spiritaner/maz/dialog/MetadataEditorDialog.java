package de.spiritaner.maz.dialog;

import de.spiritaner.maz.model.meta.MetaClass;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

/**
 * Created by florian on 2/26/17.
 */
public class MetadataEditorDialog {

    public static Optional<String> showAndWait(MetaClass metaClassObj, String metaName) {
        TextInputDialog dialog;

        if(metaClassObj == null) {
            dialog = new TextInputDialog();
            dialog.setTitle(metaName + " anlegen");
        } else {
            dialog = new TextInputDialog(metaClassObj.getDescription());
            dialog.setTitle(metaName + " bearbeiten");
        }

        dialog.setHeaderText(null);
        dialog.setContentText("Beschreibung: ");

        return dialog.showAndWait();
    }
}

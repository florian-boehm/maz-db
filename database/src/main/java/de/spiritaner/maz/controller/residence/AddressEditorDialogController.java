package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.view.dialog.EditorDialog;
import javafx.scene.layout.GridPane;
import org.apache.log4j.Logger;

@EditorDialog.Annotation(fxmlFile = "/fxml/residence/address_editor_dialog.fxml", objDesc = "$address")
public class AddressEditorDialogController extends EditorDialogController<Address> {

    final static Logger logger = Logger.getLogger(AddressEditorDialogController.class);

    public GridPane addressEditor;
    public AddressEditorController addressEditorController;

    @Override
    protected void bind(Address address) {
        addressEditorController.address.bindBidirectional(identifiable);
    }

    @Override
    protected boolean allValid() {
        boolean addressValid = addressEditorController.isValid();

        return addressValid;
    }
}

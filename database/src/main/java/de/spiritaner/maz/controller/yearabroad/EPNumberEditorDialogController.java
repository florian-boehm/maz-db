package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.model.EPNumber;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.view.dialog.EditorDialog;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/yearabroad/epnumber_editor_dialog.fxml", objDesc = "$ep_number")
public class EPNumberEditorDialogController extends EditorDialogController<EPNumber> {

	final static Logger logger = Logger.getLogger(EPNumberEditorDialogController.class);

	public GridPane epNumberEditor;
	public EPNumberEditorController epNumberEditorController;

	@Override
	protected boolean allValid() {
		boolean epNumberValid = epNumberEditorController.isValid();

		return epNumberValid;
	}

	@Override
	protected void bind(EPNumber epNumber) {
		epNumberEditorController.epNumber.bindBidirectional(identifiable);
	}
}

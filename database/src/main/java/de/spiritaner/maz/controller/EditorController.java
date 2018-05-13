package de.spiritaner.maz.controller;

import de.spiritaner.maz.view.binding.AutoBinder;
import de.spiritaner.maz.view.validation.AutoValidator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.util.ResourceBundle;

abstract public class EditorController implements Controller {

	final static Logger logger = Logger.getLogger(EditorController.class);

	protected final ResourceBundle guiText = ResourceBundle.getBundle("lang.gui");

	public BooleanProperty readOnly = new SimpleBooleanProperty(false);

	public final AutoValidator autoValidator;
	public final AutoBinder autoBinder;

	public EditorController() {
		autoValidator = new AutoValidator();
		autoBinder = new AutoBinder();
	}

	@Override
	public void setStage(Stage stage) {
		// TODO Implement correctly
	}

	@Override
	public void onReopen() {
		// TODO Implement correctly
	}

	public boolean isValid() {
		return autoValidator.validateAll();
	}
}

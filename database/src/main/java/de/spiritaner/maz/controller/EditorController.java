package de.spiritaner.maz.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

abstract public class EditorController implements Controller {

	final static Logger logger = Logger.getLogger(EditorController.class);

	public BooleanProperty readOnly = new SimpleBooleanProperty(false);

	@Override
	public void setStage(Stage stage) {
		// TODO Implement correctly
	}

	@Override
	public void onReopen() {
		// TODO Implement correctly
	}
}

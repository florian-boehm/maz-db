package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.model.EPNumber;
import de.spiritaner.maz.util.validator.TextValidator;
import de.spiritaner.maz.view.binding.AutoBinder;
import de.spiritaner.maz.view.component.BindableTextField;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class EPNumberEditorController extends EditorController {

	public ObjectProperty<EPNumber> epNumber = new SimpleObjectProperty<>();

	public BindableTextField epNumberField;
	public BindableTextField descriptionField;

	private TextValidator epNumberValidator;
	private TextValidator descriptionFieldValidator;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		AutoBinder ab = new AutoBinder(this);
		epNumber.addListener((observable, oldValue, newValue) -> ab.rebindAll());

		// TODO Extract strings
		epNumberValidator = TextValidator.create(epNumberField).fieldName("EP-Nummer").notEmpty(true).validateOnChange();
		descriptionFieldValidator = TextValidator.create(descriptionField).fieldName("Beschreibung").notEmpty(true).validateOnChange();
	}

	@Override
	public boolean isValid() {
		boolean epNumberValid = epNumberValidator.validate();
		boolean descriptionValid = descriptionFieldValidator.validate();

		return epNumberValid && descriptionValid;
	}
}

package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.model.Site;
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

public class SiteEditorController extends EditorController {

	public ObjectProperty<Site> site = new SimpleObjectProperty<>();

	public BindableTextField nameField;
	public BindableTextField organizationField;

	private TextValidator nameValidator;
	private TextValidator organizationValidator;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		AutoBinder ab = new AutoBinder(this);
		site.addListener((observable, oldValue, newValue) -> ab.rebindAll());

		nameValidator = TextValidator.create(nameField).notEmpty(true).fieldName("Name").validateOnChange();
		organizationValidator = TextValidator.create(organizationField).notEmpty(true).fieldName("Organisation").validateOnChange();
	}

	@Override
	public boolean isValid() {
		boolean nameValid = nameValidator.validate();
		boolean organizationValid = organizationValidator.validate();

		return nameValid && organizationValid;
	}
}

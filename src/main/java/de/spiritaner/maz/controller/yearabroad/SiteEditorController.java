package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.model.Site;
import de.spiritaner.maz.util.validator.TextValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SiteEditorController implements Initializable{

	@FXML
	private TextField nameField;
	@FXML
	private TextField organizationField;

	private TextValidator nameValidator;
	private TextValidator organizationValidator;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		nameValidator = TextValidator.create(nameField).notEmpty(true).fieldName("Name").validateOnChange();
		organizationValidator = TextValidator.create(organizationField).notEmpty(true).fieldName("Organisation").validateOnChange();
	}

	public void setAll(Site site) {
		nameField.setText(site.getName());
		organizationField.setText(site.getOrganization());
	}

	public Site getAll(Site site) {
		if(site == null) site = new Site();
		site.setName(nameField.getText());
		site.setOrganization(organizationField.getText());
		return site;
	}

	public void setReadonly(boolean readonly) {
		nameField.setDisable(readonly);
		organizationField.setDisable(readonly);
	}

	public boolean isValid() {
		boolean nameValid = nameValidator.validate();
		boolean organizationValid = organizationValidator.validate();

		return nameValid && organizationValid;
	}
}

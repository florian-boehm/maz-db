package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.model.Site;
import de.spiritaner.maz.util.validator.NotEmpty;
import de.spiritaner.maz.view.component.BindableTextField;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.net.URL;
import java.util.ResourceBundle;

public class SiteEditorController extends EditorController {

	public ObjectProperty<Site> site = new SimpleObjectProperty<>();

	public BindableTextField nameField;
	public BindableTextField organizationField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		autoBinder.register(this);
		site.addListener((observable, oldValue, newValue) -> autoBinder.rebindAll());
		autoValidator.add(nameField, new NotEmpty(nameField, guiText.getString("name")));
		autoValidator.add(organizationField, new NotEmpty(organizationField, guiText.getString("organisation")));
	}
}

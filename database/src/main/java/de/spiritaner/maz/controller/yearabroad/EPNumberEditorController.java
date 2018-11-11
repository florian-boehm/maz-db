package de.spiritaner.maz.controller.yearabroad;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.model.EPNumber;
import de.spiritaner.maz.util.validator.NotEmpty;
import de.spiritaner.maz.view.component.BindableTextField;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.net.URL;
import java.util.ResourceBundle;

public class EPNumberEditorController extends EditorController {

	public ObjectProperty<EPNumber> epNumber = new SimpleObjectProperty<>();

	public BindableTextField epNumberField;
	public BindableTextField descriptionField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		autoBinder.register(this);
		epNumber.addListener((observable, oldValue, newValue) -> autoBinder.rebindAll());

		autoValidator.add(epNumberField, new NotEmpty(epNumberField, guiText.getString("ep_number")));
		autoValidator.add(descriptionField, new NotEmpty(descriptionField, guiText.getString("description")));
	}
}

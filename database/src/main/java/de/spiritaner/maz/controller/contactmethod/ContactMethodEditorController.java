package de.spiritaner.maz.controller.contactmethod;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.meta.ContactMethodTypeOverviewController;
import de.spiritaner.maz.model.ContactMethod;
import de.spiritaner.maz.model.meta.ContactMethodType;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.NotEmpty;
import de.spiritaner.maz.util.validator.PopOverVisitor;
import de.spiritaner.maz.util.validator.Selected;
import de.spiritaner.maz.view.binding.BindableProperty;
import de.spiritaner.maz.view.component.BindableComboBox;
import de.spiritaner.maz.view.component.BindableTextField;
import de.spiritaner.maz.view.component.BindableToggleSwitch;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class ContactMethodEditorController extends EditorController {

	@BindableProperty
	public ObjectProperty<ContactMethod> contactMethod = new SimpleObjectProperty<>();

	public BindableToggleSwitch preferredToggleSwitch;
	public BindableTextField remarkField;
	public BindableTextField valueField;
	public BindableComboBox<ContactMethodType> contactMethodTypeComboBox;
	public Button addNewContactMethodTypeButton;

	public void initialize(URL location, ResourceBundle resources) {
		// Bind all bindable fields to the bindable property
		autoBinder.register(this);
		contactMethod.addListener((observable, oldValue, newValue) -> autoBinder.rebindAll());

		// Change the validator visitor to PopOver and add Validations as well as change listeners
		autoValidator.visitor = new PopOverVisitor();
		autoValidator.add(new NotEmpty(valueField, guiText.getString("value")));
		autoValidator.add(new Selected(contactMethodTypeComboBox, guiText.getString("contact_method")));
		autoValidator.validateOnChange(valueField);
		autoValidator.validateOnChange(contactMethodTypeComboBox);

		loadContactMethodType();
	}

	private void loadContactMethodType() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<ContactMethodType> result = em.createNamedQuery("ContactMethodType.findAll", ContactMethodType.class).getResultList();

		contactMethodTypeComboBox.populate(result, null);
	}

	public void addNewContactMethodType(ActionEvent actionEvent) {
		new ContactMethodTypeOverviewController().create(actionEvent);

		loadContactMethodType();
	}
}

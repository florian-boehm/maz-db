package de.spiritaner.maz.controller.contactmethod;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.meta.ContactMethodTypeOverviewController;
import de.spiritaner.maz.model.ContactMethod;
import de.spiritaner.maz.model.meta.ContactMethodType;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.ComboBoxValidator;
import de.spiritaner.maz.util.validator.TextValidator;
import de.spiritaner.maz.view.binding.AutoBinder;
import de.spiritaner.maz.view.binding.BindableProperty;
import de.spiritaner.maz.view.component.BindableComboBox;
import de.spiritaner.maz.view.component.BindableTextField;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import org.apache.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class ContactMethodEditorController extends EditorController {

	final static Logger logger = Logger.getLogger(ContactMethodEditorController.class);

	@BindableProperty
	public ObjectProperty<ContactMethod> contactMethod = new SimpleObjectProperty<>();

	public ToggleSwitch preferredToggleSwitch;
	public BindableTextField remarkField;
	public BindableTextField valueField;
	public BindableComboBox<ContactMethodType> contactMethodTypeComboBox;
	public Button addNewContactMethodTypeButton;

	private TextValidator valueFieldValidator;
	private ComboBoxValidator<ContactMethodType> contactMethodTypeValidator;

	public void initialize(URL location, ResourceBundle resources) {
		AutoBinder ab = new AutoBinder(this);
		contactMethod.addListener((observable, oldValue, newValue) -> ab.rebindAll());

		// TODO Extract strings
		valueFieldValidator = TextValidator.create(valueField).fieldName("Wert").notEmpty(true).validateOnChange();
		contactMethodTypeValidator = new ComboBoxValidator<>(contactMethodTypeComboBox).fieldName("Kontaktart").isSelected(true).validateOnChange();

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

	@Override
	public boolean isValid() {
		boolean valueValid = valueFieldValidator.validate();
		boolean contactMethodTypeValid = contactMethodTypeValidator.validate();

		return valueValid && contactMethodTypeValid;
	}
}

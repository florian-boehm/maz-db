package de.spiritaner.maz.controller.contactmethod;

import de.spiritaner.maz.controller.meta.ContactMethodTypeOverviewController;
import de.spiritaner.maz.model.ContactMethod;
import de.spiritaner.maz.model.meta.ContactMethodType;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.ComboBoxValidator;
import de.spiritaner.maz.util.validator.TextValidator;
import de.spiritaner.maz.view.renderer.MetaClassListCell;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class ContactMethodEditorController implements Initializable {

	final static Logger logger = Logger.getLogger(ContactMethodEditorController.class);

	@FXML
	private ToggleSwitch preferredToggleSwitch;
	@FXML
	private TextField remarkField;
	@FXML
	private TextField valueField;
	@FXML
	private ComboBox<ContactMethodType> contactMethodTypeComboBox;
	@FXML
	private Button addNewContactMethodTypeButton;

	private TextValidator valueFieldValidator;
	private ComboBoxValidator<ContactMethodType> contactMethodTypeValidator;

	public void initialize(URL location, ResourceBundle resources) {
		valueFieldValidator = TextValidator.create(valueField).fieldName("Wert").notEmpty(true).validateOnChange();
		contactMethodTypeValidator = new ComboBoxValidator<>(contactMethodTypeComboBox).fieldName("Kontaktart").isSelected(true).validateOnChange();

		contactMethodTypeComboBox.setCellFactory(column -> new MetaClassListCell<>());
		contactMethodTypeComboBox.setButtonCell(new MetaClassListCell<>());

		loadContactMethodType();
	}

	public void setAll(ContactMethod contactMethod) {
		valueField.setText(contactMethod.getValue());
		contactMethodTypeComboBox.setValue(contactMethod.getContactMethodType());
		remarkField.setText(contactMethod.getRemark());
		preferredToggleSwitch.setSelected(contactMethod.isPreferred());
	}

	public ContactMethod getAll(ContactMethod contactMethod) {
		if (contactMethod == null) contactMethod = new ContactMethod();
		contactMethod.setValue(valueField.getText());
		contactMethod.setContactMethodType(contactMethodTypeComboBox.getValue());
		contactMethod.setPreferred(preferredToggleSwitch.isSelected());
		contactMethod.setRemark(remarkField.getText());
		return contactMethod;
	}

	public void setReadonly(boolean readonly) {
		valueField.setDisable(readonly);
		contactMethodTypeComboBox.setDisable(readonly);
		addNewContactMethodTypeButton.setDisable(readonly);
		remarkField.setDisable(readonly);
		preferredToggleSwitch.setDisable(readonly);
	}

	public void loadContactMethodType() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<ContactMethodType> result = em.createNamedQuery("ContactMethodType.findAll", ContactMethodType.class).getResultList();

		ContactMethodType selectedBefore = contactMethodTypeComboBox.getValue();
		contactMethodTypeComboBox.getItems().clear();
		contactMethodTypeComboBox.getItems().addAll(FXCollections.observableArrayList(result));
		contactMethodTypeComboBox.setValue(selectedBefore);
	}

	public boolean isValid() {
		boolean valueValid = valueFieldValidator.validate();
		boolean contactMethodTypeValid = contactMethodTypeValidator.validate();

		return valueValid && contactMethodTypeValid;
	}

	public void addNewContactMethodType(ActionEvent actionEvent) {
		new ContactMethodTypeOverviewController().create(actionEvent);

		loadContactMethodType();
	}
}

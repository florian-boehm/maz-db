
package de.spiritaner.maz.controller.relationship;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.meta.RelationshipTypeOverviewController;
import de.spiritaner.maz.model.Relationship;
import de.spiritaner.maz.model.meta.RelationshipType;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.ComboBoxValidator;
import de.spiritaner.maz.util.validator.TextValidator;
import de.spiritaner.maz.view.binding.AutoBinder;
import de.spiritaner.maz.view.component.BindableComboBox;
import de.spiritaner.maz.view.component.BindableTextField;
import de.spiritaner.maz.view.renderer.MetaClassListCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

public class RelationshipEditorController extends EditorController {

	final static Logger logger = Logger.getLogger(RelationshipEditorController.class);

	public ObjectProperty<Relationship> relationship = new SimpleObjectProperty<>();

	public Button addNewRelationshipTypeButton;
	public BindableTextField toPersonFirstNameField;
	public BindableTextField toPersonFamilyNameField;
	public BindableComboBox<RelationshipType> relationshipTypeComboBox;
	public ToggleSwitch personFromDatabaseToggleSwitch;
	public ToggleSwitch inverseRelationshipToggleSwitch;
	public BindableComboBox<RelationshipType> inverseRelationshipTypeComboBox;
	public Button addNewInverseRelationshipTypeButton;

	private ComboBoxValidator<RelationshipType> relationshipTypeValidator;
	private ComboBoxValidator<RelationshipType> inverseRelationshipTypeValidator;
	private TextValidator toPersonFirstNameValidator;
	private TextValidator toPersonFamilyNameValidator;

	public void initialize(URL location, ResourceBundle resources) {
		AutoBinder ab = new AutoBinder(this);
		relationship.addListener((observableValue, oldValue, newValue) -> ab.rebindAll());

		relationshipTypeValidator = new ComboBoxValidator<>(relationshipTypeComboBox).fieldName("Beziehungsart").isSelected(true).validateOnChange();
		inverseRelationshipTypeValidator = new ComboBoxValidator<>(inverseRelationshipTypeComboBox).fieldName("Beziehungsart").isSelected(true).validateOnChange();
		toPersonFamilyNameValidator = TextValidator.create(toPersonFamilyNameField).fieldName("Nachname").notEmpty(true).validateOnChange();
		toPersonFirstNameValidator = TextValidator.create(toPersonFirstNameField).fieldName("Vorname").notEmpty(true).validateOnChange();

		personFromDatabaseToggleSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
			setPersonFromDatabase(newValue);
		});

		inverseRelationshipToggleSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
			addNewInverseRelationshipTypeButton.setDisable(!newValue);
			inverseRelationshipTypeComboBox.setDisable(!newValue);

			if(!newValue) inverseRelationshipTypeComboBox.setValue(null);
		});

		setPersonFromDatabase(personFromDatabaseToggleSwitch.isSelected());
		inverseRelationshipTypeComboBox.setDisable(true);
		addNewInverseRelationshipTypeButton.setDisable(true);

		loadRelationshipType();
	}


	public void loadRelationshipType() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<RelationshipType> result = em.createNamedQuery("RelationshipType.findAll", RelationshipType.class).getResultList();

		relationshipTypeComboBox.populate(result, null);
		inverseRelationshipTypeComboBox.populate(result, null);
	}

	@Override
	public boolean isValid() {
		boolean relationshipTypeValid = relationshipTypeValidator.validate();
		boolean toPersonFirstNameValid = personFromDatabaseToggleSwitch.isSelected() || toPersonFirstNameValidator.validate();
		boolean toPersonFamilyNameValid = personFromDatabaseToggleSwitch.isSelected() || toPersonFamilyNameValidator.validate();
		boolean inverseRelationshipTypeValid = !(personFromDatabaseToggleSwitch.isSelected() && inverseRelationshipToggleSwitch.isSelected()) || inverseRelationshipTypeValidator.validate();

		return relationshipTypeValid && toPersonFirstNameValid && toPersonFamilyNameValid && inverseRelationshipTypeValid;
	}

	public void addNewRelationshipType(ActionEvent actionEvent) {
		new RelationshipTypeOverviewController().create(actionEvent);

		loadRelationshipType();
	}

	public void setPersonFromDatabase(boolean personFromDatabase) {
		readOnly.set(personFromDatabase);
	}

	public ToggleSwitch getPersonFromDatabaseToggleSwitch() {
		return personFromDatabaseToggleSwitch;
	}

	public ToggleSwitch getInverseRelationshipToggleSwitch() {
		return inverseRelationshipToggleSwitch;
	}

	public RelationshipType getInverseRelationship() {
		return inverseRelationshipTypeComboBox.getValue();
	}
}

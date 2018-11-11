
package de.spiritaner.maz.controller.relationship;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.meta.RelationshipTypeOverviewController;
import de.spiritaner.maz.model.Relationship;
import de.spiritaner.maz.model.meta.RelationshipType;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.Selected;
import de.spiritaner.maz.util.validator.TextValidation;
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

	private Selected relationshipTypeValidator;
	private Selected inverseRelationshipTypeValidator;
	private TextValidation toPersonFirstNameValidator;
	private TextValidation toPersonFamilyNameValidator;

	public void initialize(URL location, ResourceBundle resources) {
		autoBinder.register(this);
		relationship.addListener((observableValue, oldValue, newValue) -> autoBinder.rebindAll());

		relationshipTypeValidator = new Selected(relationshipTypeComboBox, "Beziehungsart");
		inverseRelationshipTypeValidator = new Selected(inverseRelationshipTypeComboBox, "Beziehungsart");
		toPersonFamilyNameValidator = TextValidation.create(toPersonFamilyNameField, guiText.getString("family_name")).notEmpty(true).validateOnChange();
		toPersonFirstNameValidator = TextValidation.create(toPersonFirstNameField, guiText.getString("first_name")).notEmpty(true).validateOnChange();

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
		boolean relationshipTypeValid = relationshipTypeValidator.isValid();
		boolean toPersonFirstNameValid = personFromDatabaseToggleSwitch.isSelected() || toPersonFirstNameValidator.isValid();
		boolean toPersonFamilyNameValid = personFromDatabaseToggleSwitch.isSelected() || toPersonFamilyNameValidator.isValid();
		boolean inverseRelationshipTypeValid = !(personFromDatabaseToggleSwitch.isSelected() && inverseRelationshipToggleSwitch.isSelected()) || inverseRelationshipTypeValidator.isValid();

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


package de.spiritaner.maz.controller.relationship;

import de.spiritaner.maz.controller.meta.RelationshipTypeOverviewController;
import de.spiritaner.maz.model.Relationship;
import de.spiritaner.maz.model.meta.RelationshipType;
import de.spiritaner.maz.util.DataDatabase;
import de.spiritaner.maz.util.factory.MetaClassListCell;
import de.spiritaner.maz.util.validator.ComboBoxValidator;
import de.spiritaner.maz.util.validator.TextValidator;
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

public class RelationshipEditorController implements Initializable {

	final static Logger logger = Logger.getLogger(RelationshipEditorController.class);

	@FXML
	private Button addNewRelationshipTypeButton;
	@FXML
	private TextField toPersonFirstNameField;
	@FXML
	private TextField toPersonFamilyNameField;
	@FXML
	private ComboBox<RelationshipType> relationshipTypeComboBox;
	@FXML
	private ToggleSwitch personFromDatabaseToggleSwitch;
	@FXML
	private ToggleSwitch inverseRelationshipToggleSwitch;
	@FXML
	private ComboBox<RelationshipType> inverseRelationshipTypeComboBox;
	@FXML
	private Button addNewInverseRelationshipTypeButton;

	private ComboBoxValidator<RelationshipType> relationshipTypeValidator;
	private ComboBoxValidator<RelationshipType> inverseRelationshipTypeValidator;
	private TextValidator toPersonFirstNameValidator;
	private TextValidator toPersonFamilyNameValidator;

	public void initialize(URL location, ResourceBundle resources) {
		relationshipTypeValidator = new ComboBoxValidator<>(relationshipTypeComboBox).fieldName("Beziehungsart").isSelected(true).validateOnChange();
		inverseRelationshipTypeValidator = new ComboBoxValidator<>(inverseRelationshipTypeComboBox).fieldName("Beziehungsart").isSelected(true).validateOnChange();
		toPersonFamilyNameValidator = TextValidator.create(toPersonFamilyNameField).fieldName("Nachname").notEmpty(true).validateOnChange();
		toPersonFirstNameValidator = TextValidator.create(toPersonFirstNameField).fieldName("Vorname").notEmpty(true).validateOnChange();

		relationshipTypeComboBox.setCellFactory(column -> new MetaClassListCell<>());
		relationshipTypeComboBox.setButtonCell(new MetaClassListCell<>());

		inverseRelationshipTypeComboBox.setCellFactory(column -> new MetaClassListCell<>());
		inverseRelationshipTypeComboBox.setButtonCell(new MetaClassListCell<>());

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

	public void setAll(Relationship relationship) {
		toPersonFamilyNameField.setText(relationship.getToPersonFamilyName());
		toPersonFirstNameField.setText(relationship.getToPersonFirstName());
		relationshipTypeComboBox.setValue(relationship.getRelationshipType());
	}

	public Relationship getAll(Relationship relationship) {
		if (relationship == null) relationship = new Relationship();
		relationship.setToPersonFamilyName(toPersonFamilyNameField.getText());
		relationship.setToPersonFirstName(toPersonFirstNameField.getText());
		relationship.setRelationshipType(relationshipTypeComboBox.getValue());
		return relationship;
	}

	public void setReadonly(boolean readonly) {
		toPersonFamilyNameField.setDisable(readonly);
		toPersonFirstNameField.setDisable(readonly);
		relationshipTypeComboBox.setDisable(readonly);
		addNewRelationshipTypeButton.setDisable(readonly);
		inverseRelationshipToggleSwitch.setDisable(readonly);
		inverseRelationshipTypeComboBox.setDisable(readonly);
		addNewInverseRelationshipTypeButton.setDisable(readonly);
	}

	public void loadRelationshipType() {
		EntityManager em = DataDatabase.getFactory().createEntityManager();
		Collection<RelationshipType> result = em.createNamedQuery("RelationshipType.findAll", RelationshipType.class).getResultList();

		RelationshipType selectedBefore = relationshipTypeComboBox.getValue();
		relationshipTypeComboBox.getItems().clear();
		relationshipTypeComboBox.getItems().addAll(FXCollections.observableArrayList(result));
		relationshipTypeComboBox.setValue(selectedBefore);

		selectedBefore = inverseRelationshipTypeComboBox.getValue();
		inverseRelationshipTypeComboBox.getItems().clear();
		inverseRelationshipTypeComboBox.getItems().addAll(FXCollections.observableArrayList(result));
		inverseRelationshipTypeComboBox.setValue(selectedBefore);
	}

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
		toPersonFirstNameField.setDisable(personFromDatabase);
		toPersonFamilyNameField.setDisable(personFromDatabase);
		inverseRelationshipToggleSwitch.setDisable(!personFromDatabase);
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

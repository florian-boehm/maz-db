package de.spiritaner.maz.controller.relationship;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.controller.person.PersonOverviewController;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Relationship;
import de.spiritaner.maz.view.dialog.EditorDialog;
import de.spiritaner.maz.view.dialog.OverviewDialog;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/relationship/relationship_editor_dialog.fxml", objDesc = "$relationship")
public class RelationshipEditorDialogController extends EditorDialogController<Relationship> {

	final static Logger logger = Logger.getLogger(RelationshipEditorDialogController.class);

	public GridPane fromPersonEditor;
	public PersonEditorController fromPersonEditorController;
	public GridPane toPersonEditor;
	public PersonEditorController toPersonEditorController;
	public GridPane relationshipEditor;
	public RelationshipEditorController relationshipEditorController;
	public Button searchPersonButton;

	@Override
	protected void bind(Relationship relationship) {
		// Check if a person is already set in this residence
		fromPersonEditorController.person.bindBidirectional(relationship.fromPerson);
		fromPersonEditorController.readOnly.bind(relationship.fromPerson.isNotNull());

		relationshipEditorController.relationship.bindBidirectional(identifiable);
		toPersonEditorController.readOnly.set(true);
		toPersonEditorController.person.bindBidirectional(relationship.toPerson);
		relationshipEditorController.personFromDatabaseToggleSwitch.selectedProperty().bind(relationship.toPerson.isNotNull());
		relationshipEditorController.readOnly.bind(relationship.toPerson.isNotNull());

		// TODO Check if it is correctly implemented
		/*if (relationship.getId() != 0L) {
			if(relationship.getToPerson() != null) {
				relationshipEditorController.getPersonFromDatabaseToggleSwitch().setSelected(true);

				relationshipEditorController.setPersonFromDatabase(true);
			} else {
				relationshipEditorController.personFromDatabaseToggleSwitch.selectedProperty();
				//getPersonFromDatabaseToggleSwitch().setSelected(false);
				relationshipEditorController.setPersonFromDatabase(false);
			}

			relationshipEditorController.getPersonFromDatabaseToggleSwitch().setDisable(true);
			relationshipEditorController.getInverseRelationshipToggleSwitch().setDisable(true);
			searchPersonButton.setDisable(true);
		} else {
			relationshipEditorController.setPersonFromDatabase(true);
		}*/
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		relationshipEditorController.setPersonFromDatabase(true);

		relationshipEditorController.getPersonFromDatabaseToggleSwitch().selectedProperty().addListener((observable, oldValue, newValue) -> {
			searchPersonButton.setDisable(!newValue);

			if(!newValue) toPersonEditorController.person.set(new Person());
		});
	}

	@Override
	protected boolean allValid() {
		boolean toPersonValid = !relationshipEditorController.getPersonFromDatabaseToggleSwitch().isSelected() || toPersonEditorController.isValid();
		boolean fromPersonValid = fromPersonEditorController.isValid();
		boolean relationshipValid = relationshipEditorController.isValid();

		return toPersonValid && fromPersonValid && relationshipValid;
	}

	@Override
	protected void preSave(EntityManager em) {
		// TODO Implement correctly!
		/*
		if(relationshipEditorController.getInverseRelationshipToggleSwitch().isSelected()) {
						Relationship inverseRelationship = new Relationship();
						inverseRelationship.setToPerson(getIdentifiable().getFromPerson());
						inverseRelationship.setFromPerson(getIdentifiable().getToPerson());
						inverseRelationship.setRelationshipType(relationshipEditorController.getInverseRelationship());
						managedInverseRelationship = (!em.contains(inverseRelationship)) ? em.merge(inverseRelationship) : inverseRelationship;
					}

		em.getTransaction().commit();

		if(managedRelationship != null) {
			if(!getIdentifiable().getFromPerson().getRelationships().contains(managedRelationship)) getIdentifiable().getFromPerson().getRelationships().add(managedRelationship);
		}

		if(managedInverseRelationship != null) {
			if(!getIdentifiable().getToPerson().getRelationships().contains(managedInverseRelationship)) getIdentifiable().getToPerson().getRelationships().add(managedInverseRelationship);
		}
		 */
	}

	public void searchPerson(ActionEvent actionEvent) {
		OverviewDialog<PersonOverviewController, Person> dialog = new OverviewDialog<>(PersonOverviewController.class);
		Optional<Person> result = dialog.showAndSelect(getStage());

		result.ifPresent((final Person selectedPerson) -> {
			if(selectedPerson.equals(getIdentifiable().getFromPerson())) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle(guiText.getString("error"));
				alert.setHeaderText(null);
				alert.setContentText(guiText.getString("relationship_error_same_person"));

				alert.showAndWait();
			} else {
				getIdentifiable().setToPerson(selectedPerson);
				toPersonEditorController.person.set(selectedPerson);
			}
		});
	}
}

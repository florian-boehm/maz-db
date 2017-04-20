package de.spiritaner.maz.controller.relationship;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.person.PersonEditorController;
import de.spiritaner.maz.controller.person.PersonOverviewController;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.dialog.OverviewDialog;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Relationship;
import de.spiritaner.maz.util.DataDatabase;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/relationship/relationship_editor_dialog.fxml", objDesc = "Beziehung")
public class RelationshipEditorDialogController extends EditorController<Relationship> {

	final static Logger logger = Logger.getLogger(RelationshipEditorDialogController.class);

	@FXML
	private Button saveRelationshipButton;
	@FXML
	private Text titleText;
	@FXML
	private GridPane fromPersonEditor;
	@FXML
	private PersonEditorController fromPersonEditorController;
	@FXML
	private GridPane toPersonEditor;
	@FXML
	private PersonEditorController toPersonEditorController;
	@FXML
	private GridPane relationshipEditor;
	@FXML
	private RelationshipEditorController relationshipEditorController;
	@FXML
	private Button searchPersonButton;

	@Override
	public void setIdentifiable(Relationship relationship) {
		super.setIdentifiable(relationship);

		if (relationship != null) {
			// Check if a person is already set in this residence
			if (relationship.getFromPerson() != null) {
				fromPersonEditorController.setAll(relationship.getFromPerson());
				fromPersonEditorController.setReadonly(true);
			}

			relationshipEditorController.setAll(relationship);
			toPersonEditorController.setReadonly(true);

			if (relationship.getId() != 0L) {
				titleText.setText("Beziehung bearbeiten");
				saveRelationshipButton.setText("Speichern");

				if(relationship.getToPerson() != null) {
					toPersonEditorController.setAll(relationship.getToPerson());
					relationshipEditorController.getPersonFromDatabaseToggleSwitch().setSelected(true);
					relationshipEditorController.setPersonFromDatabase(true);
				} else {
					relationshipEditorController.getPersonFromDatabaseToggleSwitch().setSelected(false);
					relationshipEditorController.setPersonFromDatabase(false);
				}

				relationshipEditorController.getPersonFromDatabaseToggleSwitch().setDisable(true);
				relationshipEditorController.getInverseRelationshipToggleSwitch().setDisable(true);
				searchPersonButton.setDisable(true);
			} else {
				titleText.setText("Beziehung anlegen");
				saveRelationshipButton.setText("Anlegen");

				relationshipEditorController.setPersonFromDatabase(true);
			}
		}
	}

	@Override
	public void onReopen() {
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		relationshipEditorController.setPersonFromDatabase(true);

		relationshipEditorController.getPersonFromDatabaseToggleSwitch().selectedProperty().addListener((observable, oldValue, newValue) -> {
			searchPersonButton.setDisable(!newValue);

			if(!newValue) toPersonEditorController.setAll(new Person());
		});
	}

	public void saveRelationship(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			boolean toPersonValid = !relationshipEditorController.getPersonFromDatabaseToggleSwitch().isSelected() || toPersonEditorController.isValid();
			boolean fromPersonValid = fromPersonEditorController.isValid();
			boolean relationshipValid = relationshipEditorController.isValid();

			if (toPersonValid && fromPersonValid && relationshipValid) {
				EntityManager em = DataDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				if(relationshipEditorController.getPersonFromDatabaseToggleSwitch().isSelected()) {
					getIdentifiable().setToPerson(toPersonEditorController.getAll(getIdentifiable().getToPerson()));
				} else {
					getIdentifiable().setToPerson(null);
				}

				getIdentifiable().setFromPerson(fromPersonEditorController.getAll(getIdentifiable().getFromPerson()));
				relationshipEditorController.getAll(getIdentifiable());

				try {
					Relationship managedRelationship = (!em.contains(getIdentifiable())) ? em.merge(getIdentifiable()) : getIdentifiable();
					Relationship managedInverseRelationship = null;

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

					setResult(managedRelationship);
					requestClose();
				} catch (PersistenceException e) {
					em.getTransaction().rollback();
					logger.warn(e);
				} finally {
					em.close();
				}
			}
		});
	}

	public void closeDialog(ActionEvent actionEvent) {
		Platform.runLater(() -> getStage().close());
	}

	public void searchPerson(ActionEvent actionEvent) {
		OverviewDialog<PersonOverviewController, Person> dialog = new OverviewDialog<>(PersonOverviewController.class);
		Optional<Person> result = dialog.showAndWait(getStage());

		result.ifPresent((final Person selectedPerson) -> {
			if(selectedPerson.equals(getIdentifiable().getFromPerson())) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Fehler");
				alert.setHeaderText(null);
				alert.setContentText("Eine Beziehung zwischen ein und derselben Person ist nicht möglich!");

				alert.showAndWait();
			} else {
				getIdentifiable().setToPerson(selectedPerson);
				toPersonEditorController.setAll(selectedPerson);
			}
		});
	}
}

package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.model.meta.RelationshipType;
import de.spiritaner.maz.util.DataDatabase;
import de.spiritaner.maz.util.factories.MetaClassListCell;
import de.spiritaner.maz.util.validator.TextValidator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/meta/relationshiptype_editor_dialog.fxml", objDesc = "Beziehungsart")
public class RelationshipTypeEditorDialogController extends EditorController<RelationshipType> {

	final static Logger logger = Logger.getLogger(RelationshipTypeEditorDialogController.class);

	@FXML
	private TextField relationshipDescription;
	@FXML
	private ComboBox<RelationshipType> inverseRelationshipTypeComboBox;
	@FXML
	private Button saveRelationshipTypeButton;

	private TextValidator relationshipDescriptionValidator;

	@Override
	public void onReopen() {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		relationshipDescriptionValidator = TextValidator.create(relationshipDescription).fieldName("Bezeichnung").notEmpty(true).validateOnChange();

		inverseRelationshipTypeComboBox.setCellFactory(param -> new MetaClassListCell<>());
		inverseRelationshipTypeComboBox.setButtonCell(new MetaClassListCell<>());

		loadRelationshipType();
	}

	@Override
	public void setIdentifiable(RelationshipType relationship) {
		super.setIdentifiable(relationship);

		relationshipDescription.setText(relationship.getDescription());
		inverseRelationshipTypeComboBox.setValue(relationship.getInverseRelationshipType());
		inverseRelationshipTypeComboBox.getItems().remove(relationship);

		if(relationship.getId() == 0L) {
			saveRelationshipTypeButton.setText("Anlegen");
		} else {
			saveRelationshipTypeButton.setText("Speichern");
		}
	}

	public void saveRelationshipType(ActionEvent actionEvent) {
		if(relationshipDescriptionValidator.validate()) {
			getIdentifiable().setDescription(relationshipDescription.getText());

			if(inverseRelationshipTypeComboBox.getValue() != null && inverseRelationshipTypeComboBox.getValue().getId() < 0) {
				getIdentifiable().setInverseRelationshipType(null);
			} else {
				getIdentifiable().setInverseRelationshipType(inverseRelationshipTypeComboBox.getValue());
			}

			EntityManager em = DataDatabase.getFactory().createEntityManager();
			em.getTransaction().begin();

			try {
				RelationshipType managedRelationshipType = em.merge(getIdentifiable());

				if(managedRelationshipType.getInverseRelationshipType() != null) managedRelationshipType.getInverseRelationshipType().setInverseRelationshipType(managedRelationshipType);

				em.getTransaction().commit();

				getStage().close();
			} catch (PersistenceException e) {
				em.getTransaction().rollback();
				logger.warn(e);
			} finally {
				em.close();
			}
		}
	}

	public void loadRelationshipType() {
		EntityManager em = DataDatabase.getFactory().createEntityManager();
		Collection<RelationshipType> result = em.createNamedQuery("RelationshipType.findAllWithoutInverse", RelationshipType.class).getResultList();

		RelationshipType selectedBefore = inverseRelationshipTypeComboBox.getValue();
		inverseRelationshipTypeComboBox.getItems().clear();
		inverseRelationshipTypeComboBox.getItems().addAll(FXCollections.observableArrayList(result));
		inverseRelationshipTypeComboBox.setValue(selectedBefore);
		inverseRelationshipTypeComboBox.getItems().add(RelationshipType.createEmpty());

		if(getIdentifiable() != null) {
			inverseRelationshipTypeComboBox.getItems().remove(getIdentifiable());
		}
	}

	public void closeDialog(ActionEvent actionEvent) {
		Platform.runLater(() -> getStage().close());
	}
}

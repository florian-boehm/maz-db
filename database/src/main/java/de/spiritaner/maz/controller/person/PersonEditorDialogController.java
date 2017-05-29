package de.spiritaner.maz.controller.person;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.view.dialog.EditorDialog;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.util.database.DataDatabase;
import de.spiritaner.maz.util.envers.RevisionEntity;
import de.spiritaner.maz.util.envers.RevisionEntityListCell;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/person/person_editor_dialog.fxml", objDesc = "Person")
public class PersonEditorDialogController extends EditorController<Person> {

	final static Logger logger = Logger.getLogger(PersonEditorDialogController.class);

	@FXML
	private Text titleText;
	@FXML
	private GridPane personEditor;
	@FXML
	private PersonEditorController personEditorController;
	@FXML
	private Button savePersonButton;
	@FXML
	private ComboBox<RevisionEntity<Person>> revisionList;

	public void initialize(URL location, ResourceBundle resources) {

	}

	public void closeDialog(ActionEvent actionEvent) {
		Platform.runLater(() -> getStage().close());
	}

	@Override
	public void onReopen() {

	}

	public void savePerson(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			// Check if the first name, family name and birthday are valid
			boolean validation = personEditorController.isValid();

			if (validation) {
				EntityManager em = DataDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();

				personEditorController.getAll(getIdentifiable());

				// This has to be checked here because if the person is currently at the year abroad the
				// preferred address id would be lower than zero and this would lead to an error on merge/persist!
				if(getIdentifiable().getPreferredResidence() != null && getIdentifiable().getPreferredResidence().getId() < 0) {
					getIdentifiable().setPreferredResidence(null);
				}

				try {
					Person managedPerson = (!em.contains(getIdentifiable())) ? em.merge(getIdentifiable()) : getIdentifiable();
					em.getTransaction().commit();
					setResult(managedPerson);
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

	@Override
	public void setIdentifiable(Person person) {
		super.setIdentifiable(person);

		if (person != null) {
			personEditorController.setAll(person);

			if (person.getId() != 0L) {
				titleText.setText("Person bearbeiten");
				savePersonButton.setText("Speichern");
			} else {
				titleText.setText("Person anlegen");
				savePersonButton.setText("Anlegen");
			}

			final AuditReader reader = AuditReaderFactory.get(DataDatabase.getFactory().createEntityManager());
			final List<Number> revisions = reader.getRevisions(Person.class, person.getId());

			if(revisions.size() > 0) {
				revisionList.setVisible(true);
				revisionList.setCellFactory(param -> new RevisionEntityListCell<Person>());
				revisionList.setButtonCell(new RevisionEntityListCell<Person>());
				revisionList.valueProperty().addListener((observable, oldValue, newValue) -> {
					if(newValue.getEntity() == null) {
						personEditorController.setAll(person);
						personEditorController.setReadonly(false);
						savePersonButton.setDisable(false);
					} else {
						personEditorController.setAll(newValue.getEntity());
						personEditorController.setReadonly(true);
						savePersonButton.setDisable(true);
					}
				});
			} else {
				revisionList.setVisible(false);
			}

			for(Number revision : revisions) {
				RevisionEntity<Person> tmpRevisionEntity = new RevisionEntity<>();
				tmpRevisionEntity.setEntity(reader.find(Person.class, person.getId(), revision));
				tmpRevisionEntity.setRevision(revision);
				tmpRevisionEntity.setRevisionDate(reader.getRevisionDate(revision));
				tmpRevisionEntity.initialize();

				revisionList.getItems().add(tmpRevisionEntity);
			}

			if(revisions.size() > 0) {
				revisionList.getItems().add(new RevisionEntity<Person>());
				revisionList.getSelectionModel().clearSelection();
				revisionList.getSelectionModel().selectLast();
			}
		}
	}
}

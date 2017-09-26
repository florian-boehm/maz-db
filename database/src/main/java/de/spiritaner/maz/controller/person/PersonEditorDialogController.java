package de.spiritaner.maz.controller.person;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.view.dialog.EditorDialog;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.envers.RevisionEntity;
import de.spiritaner.maz.util.envers.RevisionEntityListCell;
import de.spiritaner.maz.view.dialog.OverviewDialog;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@EditorDialog.Annotation(fxmlFile = "/fxml/person/person_editor_dialog.fxml", objDesc = "Person")
public class PersonEditorDialogController extends EditorController<Person> {

    final static Logger logger = Logger.getLogger(PersonEditorDialogController.class);

    @FXML
    private Button showHistoryButton;
    @FXML
    private Text titleText;
    @FXML
    private GridPane personEditor;
    @FXML
    private PersonEditorController personEditorController;
    @FXML
    private Button savePersonButton;

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
                EntityManager em = CoreDatabase.getFactory().createEntityManager();
                em.getTransaction().begin();

                personEditorController.getAll(getIdentifiable());

                // This has to be checked here because if the person is currently at the year abroad the
                // preferred address id would be lower than zero and this would lead to an error on merge/persist!
                if (getIdentifiable().getPreferredResidence() != null && getIdentifiable().getPreferredResidence().getId() < 0) {
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
        }
    }

    public void showHistory(ActionEvent actionEvent) {
        final OverviewDialog<PersonOverviewController, Person> overviewDialog = new OverviewDialog<>(PersonOverviewController.class);
        final AuditReader reader = AuditReaderFactory.get(CoreDatabase.getFactory().createEntityManager());
        final List<Number> revisions = reader.getRevisions(Person.class, getIdentifiable().getId());
        final List<RevisionEntity<Person>> revisionList = new ArrayList<>();
        final List<Person> revItems = new ArrayList<>();

        //revisions.forEach(revNum -> {//revisions.forEach(revNum -> {
        //    Person revItem = reader.find(Person.class, getIdentifiable().getId(), revNum);
        //    revItem.idProperty().set(revNum.longValue());
        //    revItems.add(revItem);
        //});

        for(Number revision : revisions) {
            RevisionEntity<Person> tmpRevisionEntity = new RevisionEntity<>();
            tmpRevisionEntity.setEntity(reader.find(Person.class, getIdentifiable().getId(), revision));
            tmpRevisionEntity.setRevision(revision);
            tmpRevisionEntity.setRevisionDate(reader.getRevisionDate(revision));
            tmpRevisionEntity.initialize();

            revisionList.add(tmpRevisionEntity);
            revItems.add(tmpRevisionEntity.getEntity());
        }

        overviewDialog.showHistory(getIdentifiable(), Person.class, revItems, getStage());
    }
}

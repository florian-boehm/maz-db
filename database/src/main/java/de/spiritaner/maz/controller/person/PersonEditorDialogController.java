package de.spiritaner.maz.controller.person;

import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.envers.RevisionEntity;
import de.spiritaner.maz.view.dialog.EditorDialog;
import de.spiritaner.maz.view.dialog.OverviewDialog;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;

@EditorDialog.Annotation(fxmlFile = "/fxml/person/person_editor_dialog.fxml", objDesc = "Person")
public class PersonEditorDialogController extends EditorDialogController<Person> {

    final static Logger logger = Logger.getLogger(PersonEditorDialogController.class);

    public Button showHistoryButton;
    public GridPane personEditor;
    public PersonEditorController personEditorController;

    @Override
    protected void preSave(final EntityManager em) {
        // This has to be checked here because if the person is currently at the year abroad the
        // pref erred address id would be lower than zero and this would lead to an error on merge/persist!
        if (getIdentifiable().getPreferredResidence() != null && getIdentifiable().getPreferredResidence().getId() < 0) {
            getIdentifiable().setPreferredResidence(null);
        }
    }

    @Override
    protected boolean allValid() {
        return personEditorController.isValid();
    }

    @Override
    protected void bind() {
        identifiable.bindBidirectional(personEditorController.person);
    }

    public void showHistory(ActionEvent actionEvent) {
        final OverviewDialog<PersonOverviewController, Person> overviewDialog = new OverviewDialog<>(PersonOverviewController.class);
        final AuditReader reader = AuditReaderFactory.get(CoreDatabase.getFactory().createEntityManager());
        final List<Number> revisions = reader.getRevisions(Person.class, getIdentifiable().getId());
        final List<RevisionEntity<Person>> revisionList = new ArrayList<>();
        final List<Person> revItems = new ArrayList<>();

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

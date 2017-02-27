package de.spiritaner.maz.controller;

import de.spiritaner.maz.DatabaseApp;
import de.spiritaner.maz.dialog.PersonEditorDialog;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.util.DataDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.ToggleSwitch;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import javax.persistence.EntityManager;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

public class PersonController implements Initializable, Controller {

    private static final Logger logger = Logger.getLogger(PersonController.class);

    @FXML private TableView<Person> personTable;
    @FXML private TableColumn<Person, String> firstNameColumn;
    @FXML private TableColumn<Person, String> familyNameColumn;
    @FXML private TableColumn<Person, String> birthNameColumn;
    @FXML private TableColumn<Person, String> birthplaceColumn;
    @FXML private TableColumn<Person, Long> idColumn;
    @FXML private TableColumn<Person, LocalDate> birthdayColumn;
    @FXML private TableColumn<Person, String> genderColumn;
    @FXML private MaskerPane masker;
    @FXML private ToggleSwitch personDetailsToggle;
    @FXML private SplitPane personSplitPane;

    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        familyNameColumn.setCellValueFactory(cellData -> cellData.getValue().familyNameProperty());
        birthNameColumn.setCellValueFactory(cellData -> cellData.getValue().birthNameProperty());
        genderColumn.setCellValueFactory(cellData -> cellData.getValue().getGender().descriptionProperty());
        birthdayColumn.setCellValueFactory(cellData -> cellData.getValue().birthdayProperty());
        birthplaceColumn.setCellValueFactory(cellData -> cellData.getValue().birthplaceProperty());
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        birthdayColumn.setCellFactory(column -> {
            return new TableCell<Person, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        // Format date.
                        setText(myDateFormatter.format(item));
                    }
                }
            };
        });

        personTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldPerson, newPerson) -> {
            if(newPerson != null && personDetailsToggle.isSelected()) loadPersonDetails(newPerson);
        });

        loadAllPersons();
    }

    private void loadPersonDetails(Person person) {
        AuditReader reader = AuditReaderFactory.get(DataDatabase.getFactory().createEntityManager());
        List<Number> revisions = reader.getRevisions(Person.class, person.getId());

        for(Number revision : revisions) {
            logger.info("Found revision "+revision+" for person with first name "+person.getFirstName());
            Person revPerson = reader.find(Person.class, person.getId(), revision);
            logger.info("First in this revision was: "+revPerson.getFirstName());
        }

        //Event secondRevision = reader.find( Person.class, 2L, 2 );
    }

    public void createNewPerson(ActionEvent actionEvent) {
        boolean result = PersonEditorDialog.showAndWait(null, stage);

        loadAllPersons();
    }

    public void editPerson(ActionEvent actionEvent) {
        ObservableList<Person> selectedPersons = personTable.getSelectionModel().getSelectedItems();

        if(selectedPersons.size() == 1) {
            PersonEditorDialog.showAndWait(selectedPersons.get(0), stage);
        }

        loadAllPersons();
    }

    private void loadAllPersons() {
        masker.setProgressVisible(true);
        masker.setText("Lade Personen ...");
        masker.setVisible(true);

        new Thread(new Task() {
            @Override
            protected Collection<Person> call() throws Exception {
                EntityManager em = DataDatabase.getFactory().createEntityManager();
                Collection<Person> result = em.createQuery("SELECT p FROM Person p").getResultList();
                personTable.setItems(FXCollections.observableArrayList(result));
                masker.setVisible(false);
                return result;
            }
        }).start();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void onReopen() {
        loadAllPersons();
    }
}

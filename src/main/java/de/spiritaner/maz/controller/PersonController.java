package de.spiritaner.maz.controller;

import de.spiritaner.maz.dialog.PersonEditorDialog;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.util.DataDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.ToggleSwitch;

import javax.persistence.EntityManager;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.ResourceBundle;

public class PersonController implements Initializable, Controller {

    @FXML private MaskerPane masker;
    @FXML private Button doSomething;
    @FXML private ToggleSwitch personDetailsToggle;
    @FXML private SplitPane personSplitPane;
    @FXML private TableView<Person> personTable;
    @FXML private TableColumn<Person, String> firstNameColumn;
    @FXML private TableColumn<Person, String> birthNameColumn;
    @FXML private TableColumn<Person, String> birthplaceColumn;
    @FXML private TableColumn<Person, Long> idColumn;
    @FXML private TableColumn<Person, LocalDate> birthdayColumn;
    @FXML private TableColumn<Person, String> genderColumn;
    @FXML private GridPane address1;
    @FXML private GridPane person1;

    private double previousDividerPosition = -1;

    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        personDetailsToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue == false && newValue == true) {
                personSplitPane.setDividerPosition(0,(previousDividerPosition >= 0) ? previousDividerPosition : 0.5);
            } else if(oldValue == true && newValue == false) {
                previousDividerPosition = personSplitPane.getDividerPositions()[0];
                personSplitPane.setDividerPosition(0, 1.0);
            }
        });

        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
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


        // TODO implement loading mechanism for persons
//        masker.setProgress(0.0);
//        masker.setVisible(true);
//
//        new Thread(() -> {
//            for(int i = 0; i <= 100; i++) {
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                masker.setProgress(i/100.0);
//            }
//
//            masker.setVisible(false);
//        }).start();
        loadAllPersons();
    }

    public void createNewPerson(ActionEvent actionEvent) {
        boolean result = PersonEditorDialog.showAndWait(null, stage);
    }

    public void editPerson(ActionEvent actionEvent) {
        ObservableList<Person> selectedPersons = personTable.getSelectionModel().getSelectedItems();

        if(selectedPersons.size() == 1) {
            PersonEditorDialog.showAndWait(selectedPersons.get(0), stage);
        }
    }

    public void loadAllPersons() {
        EntityManager em = DataDatabase.getFactory().createEntityManager();
        Collection<Person> result = em.createQuery("SELECT p FROM Person p").getResultList();
        personTable.setItems(FXCollections.observableArrayList(result));
    }

    public void doSomethingNow(ActionEvent actionEvent) {
        System.out.println("ACTION BABY!");
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}

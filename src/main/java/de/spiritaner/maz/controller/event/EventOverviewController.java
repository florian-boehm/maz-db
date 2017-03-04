package de.spiritaner.maz.controller.event;

import de.spiritaner.maz.controller.Controller;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.util.DataDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.controlsfx.control.MaskerPane;

import javax.persistence.TypedQuery;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collection;
import java.util.ResourceBundle;

public class EventOverviewController implements Initializable, Controller {

    @FXML private MaskerPane masker;
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> eventTypeColumn;
    @FXML private TableColumn<Event, String> nameColumn;
    @FXML private TableColumn<Event, String> descriptionColumn;
    @FXML private TableColumn<Event, LocalDate> fromDateColumn;
    @FXML private TableColumn<Event, LocalDate> toDateColumn;
    @FXML private TableColumn<Event, Long> idColumn;
    @FXML private Button removeEventButton;
    @FXML private Button editEventButton;

    private Stage stage;
    private Person person;
    
    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void onReopen() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        eventTypeColumn.setCellValueFactory(cellData -> cellData.getValue().getEventType().descriptionProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        // TODO Implement correct date formatting like in person overview controller
        fromDateColumn.setCellValueFactory(cellData -> cellData.getValue().fromDateProperty());
        toDateColumn.setCellValueFactory(cellData -> cellData.getValue().toDateProperty());
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        eventTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldPerson, newPerson) -> {
            removeEventButton.setDisable(newPerson == null);
            editEventButton.setDisable(newPerson == null);
        });

        eventTable.setRowFactory(tv -> {
            TableRow<Event> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Event selectedEvent = row.getItem();
                    // TODO Add EventEditorDialog here
                    //ResidenceEditorDialog.showAndWait(selectedEvent, stage);
                    loadEventsForPerson(person);
                }
            });

            return row;
        });
    }

    public void removeEvent(ActionEvent actionEvent) {
    }

    public void editEvent(ActionEvent actionEvent) {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        // TODO implement edit event
        //ResidenceEditorDialog.showAndWait(selectedResidence, stage);

        loadEventsForPerson(person);
    }

    public void createResidence(ActionEvent actionEvent) {
        Event newEvent = new Event();
        // TODO implement edit event dialog here
        //ResidenceEditorDialog.showAndWait(newEvent, stage);

        loadEventsForPerson(person);
    }

    public void loadEventsForPerson(Person person) {
        this.person = person;

        masker.setProgressVisible(true);
        masker.setText("Lade Veranstaltungen ...");
        masker.setVisible(true);

        new Thread(new Task() {
            @Override
            protected Collection<Event> call() throws Exception {
                TypedQuery<Event> query = DataDatabase.getFactory().createEntityManager().createNamedQuery("Event.findAllForPerson",Event.class);
                query.setParameter("person", person);
                ObservableList<Event> result = FXCollections.observableArrayList(query.getResultList());
                eventTable.setItems(result);
                masker.setVisible(false);
                return result;
            }
        }).start();
    }
}

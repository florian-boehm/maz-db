package de.spiritaner.maz.controller.residence;

import de.spiritaner.maz.controller.Controller;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Residence;
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
import java.util.Collection;
import java.util.ResourceBundle;

public class ResidenceOverviewController implements Initializable, Controller {

    @FXML
    private MaskerPane masker;
    @FXML
    private TableView<Residence> residenceTable;
    @FXML
    private TableColumn<Residence, String> residenceTypeColumn;
    @FXML
    private TableColumn<Residence, String> preferredAddressColumn;
    @FXML
    private TableColumn<Residence, String> streetColumn;
    @FXML
    private TableColumn<Residence, String> houseNumberColumn;
    @FXML
    private TableColumn<Residence, String> postCodeColumn;
    @FXML
    private TableColumn<Residence, String> cityColumn;
    @FXML
    private TableColumn<Residence, String> stateColumn;
    @FXML
    private TableColumn<Residence, String> countryColumn;
    @FXML
    private TableColumn<Residence, String> additionColumn;
    @FXML
    private TableColumn<Residence, Long> idColumn;
    @FXML
    private Button removeResidenceButton;
    @FXML
    private Button editResidenceButton;

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
        residenceTypeColumn.setCellValueFactory(cellData -> cellData.getValue().getResidenceType().descriptionProperty());
        preferredAddressColumn.setCellValueFactory(cellData -> cellData.getValue().preferredAddressProperty());
        streetColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().streetProperty());
        houseNumberColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().houseNumberProperty());
        postCodeColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().postCodeProperty());
        cityColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().cityProperty());
        stateColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().stateProperty());
        countryColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().countryProperty());
        additionColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().additionProperty());
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        residenceTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldPerson, newPerson) -> {
            removeResidenceButton.setDisable(newPerson == null);
            editResidenceButton.setDisable(newPerson == null);
        });

        residenceTable.setRowFactory(tv -> {
            TableRow<Residence> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Residence selectedResidence = row.getItem();
                    EditorDialog.showAndWait(selectedResidence,stage);
                    loadResidencesForPerson(selectedResidence.getPerson());
                }
            });

            return row;
        });

    }

    public void removeResidence(ActionEvent actionEvent) {
    }

    public void editResidence(ActionEvent actionEvent) {
        Residence selectedResidence = residenceTable.getSelectionModel().getSelectedItem();
        EditorDialog.showAndWait(selectedResidence,stage);

        loadResidencesForPerson(selectedResidence.getPerson());
    }

    public void createResidence(ActionEvent actionEvent) {
        Residence newResidence = new Residence();
        newResidence.setPerson(person);
        EditorDialog.showAndWait(newResidence,stage);

        loadResidencesForPerson(person);
    }

    public void loadResidencesForPerson(Person person) {
        this.person = person;

        masker.setProgressVisible(true);
        masker.setText("Lade Wohnorte ...");
        masker.setVisible(true);

        new Thread(new Task() {
            @Override
            protected Collection<Residence> call() throws Exception {
                TypedQuery<Residence> query = DataDatabase.getFactory().createEntityManager().createNamedQuery("Residence.findAllForPerson", Residence.class);
                query.setParameter("person", person);
                ObservableList<Residence> result = FXCollections.observableArrayList(query.getResultList());
                residenceTable.setItems(result);
                masker.setVisible(false);
                return result;
            }
        }).start();
    }
}

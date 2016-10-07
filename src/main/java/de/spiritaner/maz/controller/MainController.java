package de.spiritaner.maz.controller;

import de.spiritaner.maz.dialog.PersonEditorDialog;
import de.spiritaner.maz.model.Gender;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.util.DataDatabase;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.action.Action;

import javax.persistence.EntityManager;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * Created by Florian on 8/13/2016.
 */
public class MainController implements Initializable {

	@FXML private ToggleSwitch personDetailsToggle;
	@FXML private SplitPane personSplitPane;
	@FXML private TableView<Person> personTable;
	@FXML private TableColumn<Person, String> prenameColumn;
	@FXML private TableColumn<Person, String> surnameColumn;
	@FXML private TableColumn<Person, String> birthplaceColumn;
	@FXML private TableColumn<Person, Long> idColumn;
	@FXML private TableColumn<Person, LocalDate> birthdayColumn;
	@FXML private TableColumn<Person, String> genderColumn;
	@FXML private GridPane address1;
	@FXML private GridPane person1;

	private double previousDividerPosition = -1;

	private Stage stage;

	public void initialize(URL location, ResourceBundle resources) {
		personDetailsToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if(oldValue == false && newValue == true) {
				personSplitPane.setDividerPosition(0,(previousDividerPosition >= 0) ? previousDividerPosition : 0.5);
			} else if(oldValue == true && newValue == false) {
				previousDividerPosition = personSplitPane.getDividerPositions()[0];
				personSplitPane.setDividerPosition(0, 1.0);
			}
		});

		prenameColumn.setCellValueFactory(cellData -> cellData.getValue().prenameProperty());
		surnameColumn.setCellValueFactory(cellData -> cellData.getValue().surnameProperty());
		genderColumn.setCellValueFactory(cellData -> cellData.getValue().getGender().description());
		birthdayColumn.setCellValueFactory(cellData -> cellData.getValue().birthday());
		birthplaceColumn.setCellValueFactory(cellData -> cellData.getValue().birthplace());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().id().asObject());

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

						// Style all dates in March with a different color.
//						if (item.getMonth() == Month.MARCH) {
//							setTextFill(Color.CHOCOLATE);
//							setStyle("-fx-background-color: yellow");
//						} else {
//							setTextFill(Color.BLACK);
//							setStyle("");
//						}
					}
				}
			};
		});

//		Path path = new Path();
//		path.setStrokeWidth(2);
//
//		MoveTo moveTo = new MoveTo();
//		moveTo.setX(person1.getBoundsInParent().getMinX()-10);
//		moveTo.setY(person1.getBoundsInParent().getMinY()+person1.getHeight()/2);
//
//		CubicCurveTo cubic = new CubicCurveTo();
//		cubic.setX(address1.getBoundsInParent().getMaxX()+10);
//		cubic.setY(address1.getBoundsInParent().getMinY()+address1.getHeight()/2);
//		cubic.setControlX1(address1.getBoundsInParent().getMaxX()+10);
//		cubic.setControlY1(person1.getBoundsInParent().getMinY()+person1.getHeight()/2);
//		cubic.setControlX2(person1.getBoundsInParent().getMinX()-10);
//		cubic.setControlY2(address1.getBoundsInParent().getMinY()+address1.getHeight()/2);
//
//		path.getElements().addAll(moveTo,cubic);
//		resources.get
//		stage.getScene().getRoot().getChildrenUnmodifiable().add(path);
	}

	public void createNewPerson(ActionEvent actionEvent) {
		PersonEditorDialog.showAndWait(null, stage);
	}

	public void editPerson(ActionEvent actionEvent) {
		ObservableList<Person> selectedPersons = personTable.getSelectionModel().getSelectedItems();

		if(selectedPersons.size() == 1) {
			PersonEditorDialog.showAndWait(selectedPersons.get(0), stage);
		}
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void loadAllPersons() {
		EntityManager em = DataDatabase.getFactory().createEntityManager();
		Collection<Person> result = em.createQuery("SELECT p FROM Person p").getResultList();
		personTable.setItems(FXCollections.observableArrayList(result));
	}

	public void closeApplication(ActionEvent actionEvent) {
		Platform.exit();
	}
}

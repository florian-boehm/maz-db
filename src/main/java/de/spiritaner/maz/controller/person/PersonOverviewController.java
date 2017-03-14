package de.spiritaner.maz.controller.person;

import de.spiritaner.maz.controller.Controller;
import de.spiritaner.maz.controller.approval.ApprovalOverviewController;
import de.spiritaner.maz.controller.contactmethod.ContactMethodOverviewController;
import de.spiritaner.maz.controller.event.EventOverviewController;
import de.spiritaner.maz.controller.residence.ResidenceOverviewController;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.dialog.RemoveDialog;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.util.DataDatabase;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.ToggleSwitch;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PersonOverviewController implements Initializable, Controller {

	private static final Logger logger = Logger.getLogger(PersonOverviewController.class);

	@FXML
	private TableView<Person> personTable;
	@FXML
	private TableColumn<Person, String> firstNameColumn;
	@FXML
	private TableColumn<Person, String> familyNameColumn;
	@FXML
	private TableColumn<Person, String> birthNameColumn;
	@FXML
	private TableColumn<Person, String> birthplaceColumn;
	@FXML
	private TableColumn<Person, Long> idColumn;
	@FXML
	private TableColumn<Person, LocalDate> birthdayColumn;
	@FXML
	private TableColumn<Person, LocalDate> ageColumn;
	@FXML
	private TableColumn<Person, String> genderColumn;
	@FXML
	private TableColumn<Person, String> dioceseColumn;
	@FXML
	private MaskerPane masker;
	@FXML
	private MaskerPane detailsMasker;
	@FXML
	private ToggleSwitch personDetailsToggle;
	@FXML
	private SplitPane personSplitPane;
	@FXML
	private Button removePersonButton;
	@FXML
	private Button editPersonButton;

	@FXML
	private AnchorPane personResidences;
	@FXML
	private ResidenceOverviewController personResidencesController;

	@FXML
	private AnchorPane personContactMethods;
	@FXML
	private ContactMethodOverviewController personContactMethodsController;

	@FXML
	private AnchorPane personEvents;
	@FXML
	private EventOverviewController personEventsController;

	@FXML
	private AnchorPane personApprovals;
	@FXML
	private ApprovalOverviewController personApprovalsController;

	private Stage stage;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
		familyNameColumn.setCellValueFactory(cellData -> cellData.getValue().familyNameProperty());
		birthNameColumn.setCellValueFactory(cellData -> cellData.getValue().birthNameProperty());
		genderColumn.setCellValueFactory(cellData -> cellData.getValue().getGender().descriptionProperty());
		dioceseColumn.setCellValueFactory(cellData -> cellData.getValue().getDiocese(true).descriptionProperty());
		birthdayColumn.setCellValueFactory(cellData -> cellData.getValue().birthdayProperty());
		ageColumn.setCellValueFactory(cellData -> cellData.getValue().birthdayProperty());
		birthplaceColumn.setCellValueFactory(cellData -> cellData.getValue().birthplaceProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

		final DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

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

		final LocalDate today = LocalDate.now();

		ageColumn.setCellFactory(column -> {
			return new TableCell<Person, LocalDate>() {
				@Override
				protected void updateItem(LocalDate item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
						setStyle("");
					} else {
						// Format date.
						long years = item.until(today, ChronoUnit.YEARS);
						setText(years+" Jahre");
					}
				}
			};
		});

		// TODO Make multiselection possible later
		personTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		personTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldPerson, newPerson) -> {
			editPersonButton.setDisable(newPerson == null);
			removePersonButton.setDisable(newPerson == null);

			if (newPerson != null && personDetailsToggle.isSelected()) {
				loadPersonDetails(newPerson);
			} else if (newPerson == null) {
				detailsMasker.setVisible(true);
			}
		});

		personTable.setRowFactory(tv -> {
			TableRow<Person> row = new TableRow<>();

			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					Person selectedPerson = row.getItem();
					loadPersonDetails(selectedPerson);
					System.out.println(selectedPerson.getFirstName());
				}
			});

			return row;
		});

		personResidencesController.setStage(stage);

		loadAllPersons();
	}

	private void loadPersonDetails(Person person) {
		Platform.runLater(() -> {
			AuditReader reader = AuditReaderFactory.get(DataDatabase.getFactory().createEntityManager());
			List<Number> revisions = reader.getRevisions(Person.class, person.getId());

			for (Number revision : revisions) {
				logger.info("Found revision " + revision + " for person with first name " + person.getFirstName());
				Person revPerson = reader.find(Person.class, person.getId(), revision);
				logger.info("First in this revision was: " + revPerson.getFirstName());
			}

			detailsMasker.setVisible(false);

			personResidencesController.loadResidencesForPerson(person);
			personContactMethodsController.loadContactMethodsForPerson(person);
			personApprovalsController.loadApprovalsForPerson(person);
			//personEventsController.loadEventsForPerson(person);
		});
	}

	public void createNewPerson(ActionEvent actionEvent) {
		EditorDialog.showAndWait(new Person(), stage);

		loadAllPersons();
	}

	public void editPerson(ActionEvent actionEvent) {
		//ObservableList<Person> selectedPersons = personTable.getSelectionModel().getSelectedItems();

		//if(selectedPersons.size() == 1) {
		//    EditorDialog.showAndWait(new Person(), stage);
		//    PersonEditorDialog.showAndWait(selectedPersons.get(0), stage);
		//}
		Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
		EditorDialog.showAndWait(selectedPerson, stage);

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

	public void removePerson(ActionEvent actionEvent) {
		Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
		final Optional<ButtonType> result = RemoveDialog.create(selectedPerson, stage).showAndWait();

		if (result.get() == ButtonType.OK) {
			try {
				EntityManager em = DataDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();
				Person obsoletePerson = em.find(Person.class, selectedPerson.getId());
				em.remove(obsoletePerson);
				em.getTransaction().commit();

				loadAllPersons();
			} catch (RollbackException e) {
				// TODO show graphical error message in better way
				ExceptionDialog.show(e);
			}
		}
	}
}

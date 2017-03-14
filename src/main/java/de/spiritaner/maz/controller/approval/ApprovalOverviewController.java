package de.spiritaner.maz.controller.approval;

import de.spiritaner.maz.controller.Controller;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.dialog.RemoveDialog;
import de.spiritaner.maz.model.Approval;
import de.spiritaner.maz.model.ContactMethod;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.util.DataDatabase;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.MaskerPane;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.ResourceBundle;

public class ApprovalOverviewController implements Initializable, Controller {

	@FXML
	private MaskerPane masker;
	@FXML
	private TableView<Approval> approvalTable;
	@FXML
	private TableColumn<Approval, String> approvalTypeColumn;
	@FXML
	private TableColumn<Approval, Boolean> approvedColumn;
	@FXML
	private TableColumn<Approval, Long> idColumn;
	@FXML
	private Button removeContactMethodButton;
	@FXML
	private Button editContactMethodButton;

	private Stage stage;
	private Person person;

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void onReopen() {
		loadApprovalsForPerson();
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		approvalTypeColumn.setCellValueFactory(cellData -> cellData.getValue().getApprovalType().descriptionProperty());
		approvedColumn.setCellValueFactory(cellData -> cellData.getValue().approvedProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

		approvalTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldPerson, newPerson) -> {
			removeContactMethodButton.setDisable(newPerson == null);
			editContactMethodButton.setDisable(newPerson == null);
		});

		approvalTable.setRowFactory(tv -> {
			TableRow<Approval> row = new TableRow<>();

			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					Approval selectedApproval = row.getItem();
					EditorDialog.showAndWait(selectedApproval, stage);
					loadApprovalsForPerson();
				}
			});

			return row;
		});

		approvedColumn.setCellFactory(column -> {
			return new TableCell<Approval, Boolean>() {
				@Override
				protected void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
						setStyle("kA");
					} else {
						setText((item) ? "Ja" : "Nein");
					}
				}
			};
		});
	}

	public void loadApprovalsForPerson() {
		if(person != null) {
			masker.setProgressVisible(true);
			masker.setText("Lade Einwilligungen ...");
			masker.setVisible(true);

			new Thread(new Task() {
				@Override
				protected Collection<Approval> call() throws Exception {
					// Fetch the lazy collection list from the database
					EntityManager em = DataDatabase.getFactory().createEntityManager();
					em.getTransaction().begin();
					Hibernate.initialize(person.getApprovals());
					em.getTransaction().commit();
					Collection<Approval> result = FXCollections.observableArrayList(person.getApprovals());

					approvalTable.getItems().clear();
					approvalTable.getItems().addAll(result);
					masker.setVisible(false);
					return result;
				}
			}).start();
		}
	}

	public void removeContactMethod(ActionEvent actionEvent) {
		Approval selectedApproval = approvalTable.getSelectionModel().getSelectedItem();
		final Optional<ButtonType> result = RemoveDialog.create(selectedApproval, stage).showAndWait();

		if (result.get() == ButtonType.OK) {
			try {
				EntityManager em = DataDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();
				Approval obsoleteApproval = em.find(Approval.class, selectedApproval.getId());
				em.remove(obsoleteApproval);
				person.getApprovals().remove(selectedApproval);
				em.getTransaction().commit();

				loadApprovalsForPerson();
			} catch (RollbackException e) {
				// TODO show graphical error message in better way
				ExceptionDialog.show(e);
			}
		}
	}

	public void editContactMethod(ActionEvent actionEvent) {
		Approval selectedApproval = approvalTable.getSelectionModel().getSelectedItem();
		EditorDialog.showAndWait(selectedApproval, stage);

		loadApprovalsForPerson();
	}

	public void createContactMethod(ActionEvent actionEvent) {
		Approval newApproval = new Approval();
		newApproval.setPerson(person);
		EditorDialog.showAndWait(newApproval, stage);

		loadApprovalsForPerson();
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}

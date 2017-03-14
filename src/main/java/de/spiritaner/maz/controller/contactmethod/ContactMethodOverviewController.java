package de.spiritaner.maz.controller.contactmethod;

import de.spiritaner.maz.controller.Controller;
import de.spiritaner.maz.dialog.EditorDialog;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.dialog.RemoveDialog;
import de.spiritaner.maz.model.ContactMethod;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.util.DataDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javax.persistence.TypedQuery;
import javax.xml.crypto.Data;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import java.util.ResourceBundle;

public class ContactMethodOverviewController implements Initializable, Controller {

	@FXML
	private MaskerPane masker;
	@FXML
	private TableView<ContactMethod> contactMethodTable;
	@FXML
	private TableColumn<ContactMethod, String> contactMethodTypeColumn;
	@FXML
	private TableColumn<ContactMethod, String> valueColumn;
	@FXML
	private TableColumn<ContactMethod, Long> idColumn;
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
		loadContactMethodsForPerson();
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		contactMethodTypeColumn.setCellValueFactory(cellData -> cellData.getValue().getContactMethodType().descriptionProperty());
		valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

		contactMethodTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldPerson, newPerson) -> {
			removeContactMethodButton.setDisable(newPerson == null);
			editContactMethodButton.setDisable(newPerson == null);
		});

		contactMethodTable.setRowFactory(tv -> {
			TableRow<ContactMethod> row = new TableRow<>();

			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					ContactMethod selectedContactMethod = row.getItem();
					EditorDialog.showAndWait(selectedContactMethod, stage);
					loadContactMethodsForPerson();
				}
			});

			return row;
		});
	}

	public void loadContactMethodsForPerson() {
		if(person != null) {
			masker.setProgressVisible(true);
			masker.setText("Lade Kontaktwege ...");
			masker.setVisible(true);

			new Thread(new Task() {
				@Override
				protected Collection<ContactMethod> call() throws Exception {
					EntityManager em = DataDatabase.getFactory().createEntityManager();
					em.getTransaction().begin();
					Hibernate.initialize(person.getContactMethods());
					em.getTransaction().commit();
					Collection<ContactMethod> result = FXCollections.observableArrayList(person.getContactMethods());

					contactMethodTable.getItems().clear();
					contactMethodTable.getItems().addAll(result);
					masker.setVisible(false);
					return result;
				}
			}).start();
		}
	}

	public void removeContactMethod(ActionEvent actionEvent) {
		ContactMethod selectedContactMethod = contactMethodTable.getSelectionModel().getSelectedItem();
		final Optional<ButtonType> result = RemoveDialog.create(selectedContactMethod, stage).showAndWait();

		if (result.get() == ButtonType.OK) {
			try {
				EntityManager em = DataDatabase.getFactory().createEntityManager();
				em.getTransaction().begin();
				ContactMethod obsoleteContactMethod = em.find(ContactMethod.class, selectedContactMethod.getId());
				em.remove(obsoleteContactMethod);
				person.getContactMethods().remove(selectedContactMethod);
				em.getTransaction().commit();

				loadContactMethodsForPerson();
			} catch (RollbackException e) {
				// TODO show graphical error message in better way
				ExceptionDialog.show(e);
			}
		}
	}

	public void editContactMethod(ActionEvent actionEvent) {
		ContactMethod selectedContactMethod = contactMethodTable.getSelectionModel().getSelectedItem();
		EditorDialog.showAndWait(selectedContactMethod, stage);

		loadContactMethodsForPerson();
	}

	public void createContactMethod(ActionEvent actionEvent) {
		ContactMethod newContactMethod = new ContactMethod();
		newContactMethod.setPerson(person);
		EditorDialog.showAndWait(newContactMethod, stage);

		loadContactMethodsForPerson();
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}

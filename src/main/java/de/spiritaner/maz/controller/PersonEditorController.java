package de.spiritaner.maz.controller;

import de.spiritaner.maz.model.Gender;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.util.DataDatabase;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * Created by Florian on 8/13/2016.
 */
public class PersonEditorController implements Initializable {

	@FXML private Text titleText;
	@FXML private TextField prenameField;
	@FXML private TextField surnameField;
	@FXML private DatePicker birthdayDatePicker;
	@FXML private TextField birthplaceField;
	@FXML private ComboBox<Gender> genderComboBox;
	@FXML private Button savePersonButton;

	private Person person;
	private Stage stage;

	public void initialize(URL location, ResourceBundle resources) {

	}

	public void setPerson(Person person) {
		this.person = person;

		EntityManager em = DataDatabase.getFactory().createEntityManager();
		genderComboBox.setCellFactory(column -> { return new ListCell<Gender>() {
			@Override
			public void updateItem(Gender item, boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setText(null);
				} else {
					setText(item.getDescription());
				}
			}
		};});
		genderComboBox.setButtonCell(new ListCell<Gender>() {
			@Override protected void updateItem(Gender item, boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setText(null);
				} else {
					setText(item.getDescription());
				}
			}
		});
		genderComboBox.getItems().addAll(FXCollections.observableArrayList(em.createNamedQuery("Gender.findAll").getResultList()));

		if(person != null) {
			titleText.setText("Person bearbeiten");
			savePersonButton.setText("Speichern");
			prenameField.setText(person.getFirstName());
			surnameField.setText(person.getFamilyName());
			birthdayDatePicker.setValue(person.getBirthday());
			birthplaceField.setText(person.getBirthplace());
			genderComboBox.getSelectionModel().select(person.getGender());
		} else {
			titleText.setText("Neue Person anlegen");
			savePersonButton.setText("Anlegen");
		}
	}

	public void closeDialog(ActionEvent actionEvent) {
		stage.close();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void savePerson(ActionEvent actionEvent) {

	}

	public void loadGender() {
		EntityManager em = DataDatabase.getFactory().createEntityManager();
		Collection<Gender> result = em.createQuery("SELECT g FROM Gender g").getResultList();
		genderComboBox.setItems(FXCollections.observableArrayList(result));
	}
}

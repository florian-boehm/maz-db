package de.spiritaner.maz.controller;

import de.spiritaner.maz.model.Gender;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.util.DataDatabase;
import de.spiritaner.maz.util.TextValidator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * Created by Florian on 8/13/2016.
 */
public class PersonEditorController implements Initializable {

    final static Logger logger = Logger.getLogger(PersonEditorController.class);

	@FXML private Text titleText;
	@FXML private TextField firstnameField;
	@FXML private TextField familynameField;
    @FXML private TextField birthnameField;
	@FXML private DatePicker birthdayDatePicker;
	@FXML private TextField birthplaceField;
	@FXML private ComboBox<Gender> genderComboBox;
	@FXML private Button savePersonButton;

	private TextValidator firstnameFieldValidator;
	private TextValidator familynameFieldValidator;
    private TextValidator birthnameFieldValidator;

	private Person person;
	private Stage stage;

	public void initialize(URL location, ResourceBundle resources) {
        firstnameFieldValidator = TextValidator.create(firstnameField).fieldName("Vorname").notEmpty(true).removeAll(" ").textChanged();
        familynameFieldValidator = TextValidator.create(familynameField).fieldName("Nachname").notEmpty(true).removeAll(" ").textChanged();
        birthnameFieldValidator = TextValidator.create(birthnameField).fieldName("Geburtsname").notEmpty(true).removeAll(" ").textChanged();
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
			firstnameField.setText(person.getFirstName());
			familynameField.setText(person.getFamilyName());
			birthnameField.setText(person.getBirthName());
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
	    // Check if the username is valid
	    boolean prenameValid = firstnameFieldValidator.validate();
	    boolean surnameValid = familynameFieldValidator.validate();
	    // TODO Check if a birthdate is entered

        if(prenameValid && surnameValid) {
            EntityManager em = DataDatabase.getFactory().createEntityManager();

            if(person == null) {
                person = new Person();
                person.setFirstName(firstnameField.getText());
                person.setFamilyName(familynameField.getText());
                person.setBirthName(birthnameField.getText());
                person.setBirthplace(birthplaceField.getText());
                person.setBirthday(birthdayDatePicker.getValue());
                person.setGender(genderComboBox.getValue());
            }

            try {
                em.getTransaction().begin();
                em.persist(person);
                em.getTransaction().commit();
                stage.close();
            } catch (PersistenceException e) {
                em.getTransaction().rollback();
                logger.warn(e);
                logger.warn("Could not persist person");
            } finally {
                logger.info("Database contains person: " + em.contains(person));
                em.close();
            }
        }
	}

	public void loadGender() {
		EntityManager em = DataDatabase.getFactory().createEntityManager();
		Collection<Gender> result = em.createQuery("SELECT g FROM Gender g").getResultList();
		genderComboBox.setItems(FXCollections.observableArrayList(result));
	}
}

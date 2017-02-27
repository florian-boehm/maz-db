package de.spiritaner.maz.controller;

import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.meta.Gender;
import de.spiritaner.maz.util.DataDatabase;
import de.spiritaner.maz.util.DateValidator;
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
public class AddressEditorController implements Initializable {

    final static Logger logger = Logger.getLogger(AddressEditorController.class);

	@FXML private Text titleText;
	@FXML private TextField streetField;
	@FXML private TextField houseNumberField;
    @FXML private TextField postCodeField;
	@FXML private TextField cityField;
	@FXML private TextField stateField;
	@FXML private TextField countryField;
	@FXML private TextField additionField;
	@FXML private Button saveAddressButton;

	private TextValidator streetFieldValidator;
	private TextValidator houseNumberFieldValidator;
	private TextValidator postCodeFieldValidator;
	private TextValidator cityFieldValidator;
	private TextValidator stateFieldValidator;
	private TextValidator countryFieldValidator;
	private TextValidator additionFieldValidator;

	private Address address;
	private Stage stage;

	public void initialize(URL location, ResourceBundle resources) {
		streetFieldValidator = TextValidator.create(streetField).fieldName("Straße").notEmpty(true).textChanged();
		houseNumberFieldValidator = TextValidator.create(houseNumberField).fieldName("Hausnummer").notEmpty(true).textChanged();
		postCodeFieldValidator = TextValidator.create(postCodeField).fieldName("Postleitzahl").max(10).notEmpty(true).textChanged();
		cityFieldValidator = TextValidator.create(cityField).fieldName("Stadt").notEmpty(true).textChanged();
		//stateFieldValidator = TextValidator.create(stateField).fieldName("Bundesland").not
	}

	public void setAddress(Address address) {
		this.address = address;

		if(address != null) {
			titleText.setText("Adresse bearbeiten");
			saveAddressButton.setText("Speichern");
			streetField.setText(address.getStreet());
			houseNumberField.setText(address.getHouseNumber());
			postCodeField.setText(address.getPostCode());
			cityField.setText(address.getCity());
			stateField.setText(address.getState());
			countryField.setText(address.getCountry());
			additionField.setText(address.getAddition());
		} else {
			titleText.setText("Adresse anlegen");
			saveAddressButton.setText("Anlegen");
		}
	}

	public void closeDialog(ActionEvent actionEvent) {
		stage.close();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/*public void savePerson(ActionEvent actionEvent) {
	    // Check if the first name, family name and birthday are valid
	    boolean firstnameValid = firstnameFieldValidator.validate();
	    boolean familyNameValid = familynameFieldValidator.validate();
	    boolean birtdayValid = birthdayDateValidator.validate();

        if(firstnameValid && familyNameValid && birtdayValid) {
            EntityManager em = DataDatabase.getFactory().createEntityManager();
			em.getTransaction().begin();

			Person tmpPerson;

            if(person == null) {
				tmpPerson = new Person();
			} else {
            	tmpPerson = em.find(Person.class, person.getId());
            }

			tmpPerson.setFirstName(firstnameField.getText());
			tmpPerson.setFamilyName(familynameField.getText());
			tmpPerson.setBirthName(birthnameField.getText());
			tmpPerson.setBirthplace(birthplaceField.getText());
			tmpPerson.setBirthday(birthdayDatePicker.getValue());
			tmpPerson.setGender(genderComboBox.getValue());

            try {
            	// Persist only if the person has not existed before
            	if(person == null) em.persist(tmpPerson);

                em.getTransaction().commit();
                stage.close();
            } catch (PersistenceException e) {
                em.getTransaction().rollback();
                logger.warn(e);
            } finally {
                em.close();
            }
        }
	}*/

	public void saveAddress(ActionEvent actionEvent) {

	}
}

package de.spiritaner.maz.controller.person;

import de.spiritaner.maz.controller.meta.GenderEditorController;
import de.spiritaner.maz.dialog.MetadataEditorDialog;
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
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by Florian on 8/13/2016.
 */
public class PersonEditorController implements Initializable {

    final static Logger logger = Logger.getLogger(PersonEditorController.class);

    @FXML
    private TextField firstnameField;
    @FXML
    private TextField familynameField;
    @FXML
    private TextField birthnameField;
    @FXML
    private DatePicker birthdayDatePicker;
    @FXML
    private TextField birthplaceField;
    @FXML
    private ComboBox<Gender> genderComboBox;
    @FXML
    private Button addNewGenderButton;
    @FXML
    private ComboBox<String> dioceseComboBox;
    @FXML
    private Button addNewDioceseButton;

    private TextValidator firstnameFieldValidator;
    private TextValidator familynameFieldValidator;
    private DateValidator birthdayDateValidator;

    public void initialize(URL location, ResourceBundle resources) {
        firstnameFieldValidator = TextValidator.create(firstnameField).fieldName("Vorname").notEmpty(true).textChanged();
        familynameFieldValidator = TextValidator.create(familynameField).fieldName("Nachname").notEmpty(true).textChanged();
        birthdayDateValidator = DateValidator.create(birthdayDatePicker).fieldName("Geburtsdatum").notEmpty(true).past().valueChanged();

        genderComboBox.setCellFactory(column -> {
            return new ListCell<Gender>() {
                @Override
                public void updateItem(Gender item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.getDescription());
                    }
                }
            };
        });
        genderComboBox.setButtonCell(new ListCell<Gender>() {
            @Override
            protected void updateItem(Gender item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getDescription());
                }
            }
        });

        loadGender();
    }

    public void setAll(Person person) {
        firstnameField.setText(person.getFirstName());
        familynameField.setText(person.getFamilyName());
        birthnameField.setText(person.getBirthName());
        birthdayDatePicker.setValue(person.getBirthday());
        birthplaceField.setText(person.getBirthplace());
        genderComboBox.setValue(person.getGender());
        // TODO Diocese here
    }

    public Person getAll(Person person) {
        if (person == null) person = new Person();
        person.setFirstName(firstnameField.getText());
        person.setFamilyName(familynameField.getText());
        person.setBirthName(birthnameField.getText());
        person.setBirthday(birthdayDatePicker.getValue());
        person.setBirthplace(birthplaceField.getText());
        person.setGender(genderComboBox.getValue());
        // TODO Diocese here
        return person;
    }

    public void setReadonly(boolean readonly) {
        firstnameField.setDisable(readonly);
        familynameField.setDisable(readonly);
        birthnameField.setDisable(readonly);
        birthdayDatePicker.setDisable(readonly);
        birthplaceField.setDisable(readonly);
        genderComboBox.setDisable(readonly);
        addNewGenderButton.setDisable(readonly);
        dioceseComboBox.setDisable(readonly);
        addNewDioceseButton.setDisable(readonly);
    }

    public void loadGender() {
        EntityManager em = DataDatabase.getFactory().createEntityManager();
        Collection<Gender> result = em.createNamedQuery("Gender.findAll", Gender.class).getResultList();

        Gender selectedBefore = genderComboBox.getValue();
        genderComboBox.getItems().clear();
        genderComboBox.getItems().addAll(FXCollections.observableArrayList(result));
        genderComboBox.setValue(selectedBefore);
    }

    public boolean isValid() {
        return firstnameFieldValidator.validate() &&
                familynameFieldValidator.validate() &&
                birthdayDateValidator.validate();
    }

    public void addNewGender(ActionEvent actionEvent) {
        new GenderEditorController().create(actionEvent);

        loadGender();
    }

    public void addNewDiocese(ActionEvent actionEvent) {
    }
}

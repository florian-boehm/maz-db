package de.spiritaner.maz.controller.person;

import de.spiritaner.maz.controller.meta.DioceseOverviewController;
import de.spiritaner.maz.controller.meta.GenderOverviewController;
import de.spiritaner.maz.controller.meta.ReligionOverviewController;
import de.spiritaner.maz.controller.meta.SalutationOverviewController;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.meta.Diocese;
import de.spiritaner.maz.model.meta.Gender;
import de.spiritaner.maz.model.meta.Religion;
import de.spiritaner.maz.model.meta.Salutation;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.TextValidator;
import de.spiritaner.maz.view.renderer.DatePickerFormatter;
import de.spiritaner.maz.view.renderer.MetaClassListCell;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
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
	private ComboBox<Diocese> dioceseComboBox;
	@FXML
	private Button addNewDioceseButton;
	@FXML
	private ComboBox<Salutation> salutationComboBox;
	@FXML
	private Button addNewSalutationButton;
	@FXML
	private TextField honorificField;
	@FXML
	private ComboBox<Religion> religionComboBox;
	@FXML
	private Button addNewReligionButton;

	private TextValidator firstnameValidator;
	private TextValidator familynameValidator;
	//private DateValidator birthdayDateValidator;
	//private ComboBoxValidator genderComboBoxValidator;

	public void initialize(URL location, ResourceBundle resources) {
		firstnameValidator = TextValidator.create(firstnameField).fieldName("Vorname").notEmpty(true).validateOnChange();
		familynameValidator = TextValidator.create(familynameField).fieldName("Nachname").notEmpty(true).validateOnChange();
		//birthdayDateValidator = DateValidator.create(birthdayDatePicker).fieldName("Geburtsdatum").notEmpty(true).past().validateOnChange();
		//genderComboBoxValidator = new ComboBoxValidator<>(genderComboBox).fieldName("Geschlecht").isSelected(true).validateOnChange();

		birthdayDatePicker.setConverter(new DatePickerFormatter());

		genderComboBox.setCellFactory(column -> new MetaClassListCell<>());
		genderComboBox.setButtonCell(new MetaClassListCell<>());
		dioceseComboBox.setCellFactory(column -> new MetaClassListCell<>());
		dioceseComboBox.setButtonCell(new MetaClassListCell<>());
		salutationComboBox.setCellFactory(column -> new MetaClassListCell<>());
		salutationComboBox.setButtonCell(new MetaClassListCell<>());
		religionComboBox.setCellFactory(column -> new MetaClassListCell<>());
		religionComboBox.setButtonCell(new MetaClassListCell<>());

		loadGender();
		loadDiocese();
		loadSalutation();
		loadReligion();
	}

	public void setAll(Person person) {
		firstnameField.setText(person.getFirstName());
		familynameField.setText(person.getFamilyName());
		birthnameField.setText(person.getBirthName());
		birthdayDatePicker.setValue(person.getBirthday());
		birthplaceField.setText(person.getBirthplace());
		genderComboBox.setValue(person.getGender());
		dioceseComboBox.setValue(person.getDiocese());
		salutationComboBox.setValue(person.getSalutation());
		honorificField.setText(person.getHonorific());
		religionComboBox.setValue(person.getReligion());
	}

	public Person getAll(Person person) {
		if (person == null) person = new Person();
		person.setFirstName(firstnameField.getText());
		person.setFamilyName(familynameField.getText());
		person.setBirthName(birthnameField.getText());
		person.setBirthday(birthdayDatePicker.getValue());
		person.setBirthplace(birthplaceField.getText());
		person.setGender((genderComboBox.getSelectionModel().getSelectedIndex() <= 0) ? null : genderComboBox.getValue());
		person.setDiocese((dioceseComboBox.getSelectionModel().getSelectedIndex() <= 0) ? null : dioceseComboBox.getValue());
		person.setSalutation((salutationComboBox.getSelectionModel().getSelectedIndex() <= 0) ? null : salutationComboBox.getValue());
		person.setHonorific(honorificField.getText());
		person.setReligion((religionComboBox.getSelectionModel().getSelectedIndex() <= 0) ? null : religionComboBox.getValue());
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
		salutationComboBox.setDisable(readonly);
		addNewSalutationButton.setDisable(readonly);
		honorificField.setDisable(readonly);
		religionComboBox.setDisable(readonly);
		addNewReligionButton.setDisable(readonly);
	}

	private void loadGender() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<Gender> result = em.createNamedQuery("Gender.findAll", Gender.class).getResultList();

		Gender selectedBefore = genderComboBox.getValue();
		genderComboBox.getItems().clear();
		genderComboBox.getItems().addAll(FXCollections.observableArrayList(result));
		genderComboBox.getItems().add(0, Gender.createEmpty());
		genderComboBox.setValue(selectedBefore);
	}

	private void loadDiocese() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<Diocese> result = em.createNamedQuery("Diocese.findAll", Diocese.class).getResultList();

		Diocese selectedBefore = dioceseComboBox.getValue();
		dioceseComboBox.getItems().clear();
		dioceseComboBox.getItems().addAll(FXCollections.observableArrayList(result));
		dioceseComboBox.getItems().add(0, Diocese.createEmpty());
		dioceseComboBox.setValue(selectedBefore);
	}

	private void loadSalutation() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<Salutation> result = em.createNamedQuery("Salutation.findAll", Salutation.class).getResultList();

		Salutation selectedBefore = salutationComboBox.getValue();
		salutationComboBox.getItems().clear();
		salutationComboBox.getItems().addAll(FXCollections.observableArrayList(result));
		salutationComboBox.getItems().add(0, Salutation.createEmpty());
		salutationComboBox.setValue(selectedBefore);
	}

	private void loadReligion() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<Religion> result = em.createNamedQuery("Religion.findAll", Religion.class).getResultList();

		Religion selectedBefore = religionComboBox.getValue();
		religionComboBox.getItems().clear();
		religionComboBox.getItems().addAll(FXCollections.observableArrayList(result));
		religionComboBox.getItems().add(0, Religion.createEmpty());
		religionComboBox.setValue(selectedBefore);
	}

	public boolean isValid() {
		boolean firstnameValid = firstnameValidator.validate();
		boolean familynameValid = familynameValidator.validate();
		/*boolean birthdayValid = birthdayDateValidator.validate();*/
		/*boolean genderValid = genderComboBoxValidator.validate();*/

		return firstnameValid &&
				  familynameValid /*&&
				  birthdayValid &&
				  genderValid*/;
	}

	public void addNewGender(ActionEvent actionEvent) {
		new GenderOverviewController().create(actionEvent);

		loadGender();
	}

	public void addNewDiocese(ActionEvent actionEvent) {
		new DioceseOverviewController().create(actionEvent);

		loadDiocese();
	}

	public void addNewSalutation(ActionEvent actionEvent) {
		new SalutationOverviewController().create(actionEvent);

		loadSalutation();
	}

	public void addNewReligion(ActionEvent actionEvent) {
		new ReligionOverviewController().create(actionEvent);

		loadReligion();
	}
}

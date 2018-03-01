package de.spiritaner.maz.controller.person;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.meta.DioceseOverviewController;
import de.spiritaner.maz.controller.meta.GenderOverviewController;
import de.spiritaner.maz.controller.meta.ReligionOverviewController;
import de.spiritaner.maz.controller.meta.SalutationOverviewController;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.meta.Diocese;
import de.spiritaner.maz.model.meta.Gender;
import de.spiritaner.maz.model.meta.Religion;
import de.spiritaner.maz.model.meta.Salutation;
import de.spiritaner.maz.view.binding.AutoBinder;
import de.spiritaner.maz.view.binding.BindProperty;
import de.spiritaner.maz.view.binding.BindableProperty;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.TextValidator;
import de.spiritaner.maz.view.component.BindableComboBox;
import de.spiritaner.maz.view.component.BindableDatePicker;
import de.spiritaner.maz.view.component.BindableTextField;
import de.spiritaner.maz.view.renderer.DatePickerFormatter;
import de.spiritaner.maz.view.renderer.MetaClassListCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * Created by Florian on 8/13/2016.
 */
public class PersonEditorController extends EditorController {

	final static Logger logger = Logger.getLogger(PersonEditorController.class);

	@BindableProperty
	public ObjectProperty<Person> person = new SimpleObjectProperty<>();

	public BindableTextField firstNameField;
	public BindableTextField familyNameField;
	public BindableTextField birthNameField;
	public BindableDatePicker birthdayDatePicker;
	public BindableTextField birthplaceField;
	public BindableComboBox<Gender> genderComboBox;
	public Button addNewGenderButton;
	public BindableComboBox<Diocese> dioceseComboBox;
	public Button addNewDioceseButton;
	public BindableComboBox<Salutation> salutationComboBox;
	public Button addNewSalutationButton;
	public BindableTextField honorificField;
	public BindableComboBox<Religion> religionComboBox;
	public Button addNewReligionButton;

	private TextValidator firstNameValidator;
	private TextValidator familyNameValidator;
	//private DateValidator birthdayDateValidator;
	//private ComboBoxValidator genderComboBoxValidator;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		AutoBinder ab = new AutoBinder(this);

		person.addListener((observable, oldValue, newValue) -> ab.rebindAll());

		firstNameValidator = TextValidator.create(firstNameField).fieldName("Vorname").notEmpty(true).validateOnChange();
		familyNameValidator = TextValidator.create(familyNameField).fieldName("Nachname").notEmpty(true).validateOnChange();
		//birthdayDateValidator = DateValidator.create(birthdayDatePicker).fieldName("Geburtsdatum").notEmpty(true).past().validateOnChange();
		//genderComboBoxValidator = new ComboBoxValidator<>(genderComboBox).fieldName("Geschlecht").isSelected(true).validateOnChange();

		birthdayDatePicker.setConverter(new DatePickerFormatter());

		loadGender();
		loadDiocese();
		loadSalutation();
		loadReligion();
	}

	private void loadGender() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<Gender> result = em.createNamedQuery("Gender.findAll", Gender.class).getResultList();
		genderComboBox.populate(result, Gender.createEmpty());
	}

	private void loadDiocese() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<Diocese> result = em.createNamedQuery("Diocese.findAll", Diocese.class).getResultList();

		dioceseComboBox.populate(result, Diocese.createEmpty());
	}

	private void loadSalutation() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<Salutation> result = em.createNamedQuery("Salutation.findAll", Salutation.class).getResultList();

		salutationComboBox.populate(result, Salutation.createEmpty());
	}

	private void loadReligion() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<Religion> result = em.createNamedQuery("Religion.findAll", Religion.class).getResultList();

		religionComboBox.populate(result, Religion.createEmpty());
	}

	@Override
	public boolean isValid() {
		boolean firstnameValid = firstNameValidator.validate();
		boolean familynameValid = familyNameValidator.validate();
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

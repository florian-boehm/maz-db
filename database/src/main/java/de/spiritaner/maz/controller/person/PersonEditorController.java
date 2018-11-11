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
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.NotEmpty;
import de.spiritaner.maz.util.validator.PopOverVisitor;
import de.spiritaner.maz.view.binding.BindableProperty;
import de.spiritaner.maz.view.component.BindableComboBox;
import de.spiritaner.maz.view.component.BindableDatePicker;
import de.spiritaner.maz.view.component.BindableTextField;
import de.spiritaner.maz.view.renderer.DatePickerFormatter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * Created by Florian on 8/13/2016.
 */
public class PersonEditorController extends EditorController {

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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Bind all bindable fields to the bindable property
		autoBinder.register(this);
		person.addListener((observable, oldValue, newValue) -> autoBinder.rebindAll());

		// Change the validator visitor to PopOver and add Validations as well as change listeners
		autoValidator.visitor = new PopOverVisitor();
		autoValidator.add(new NotEmpty(firstNameField, guiText.getString("first_name")));
		autoValidator.add(new NotEmpty(familyNameField, guiText.getString("family_name")));
		autoValidator.validateOnChange(firstNameField);
		autoValidator.validateOnChange(familyNameField);

		// Custom format of some fields
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

package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.meta.EventTypeOverviewController;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.meta.EventType;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.ComboBoxValidator;
import de.spiritaner.maz.util.validator.DateValidator;
import de.spiritaner.maz.util.validator.TextValidator;
import de.spiritaner.maz.view.binding.AutoBinder;
import de.spiritaner.maz.view.component.BindableComboBox;
import de.spiritaner.maz.view.component.BindableDatePicker;
import de.spiritaner.maz.view.component.BindableTextArea;
import de.spiritaner.maz.view.component.BindableTextField;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class EventEditorController extends EditorController {

	final static Logger logger = Logger.getLogger(EventEditorController.class);

	public ObjectProperty<Event> event = new SimpleObjectProperty<>();

	public BindableTextField nameField;
	public BindableTextArea descriptionArea;
	public BindableDatePicker fromDatePicker;
	public BindableDatePicker toDatePicker;
	public BindableComboBox<EventType> eventTypeComboBox;
	public BindableTextField locationField;
	public Button addNewEventTypeButton;

	private TextValidator nameValidator;
	private ComboBoxValidator<EventType> eventTypeValidator;
	private DateValidator toDateValidator;
	private TextValidator locationValidator;

	public void initialize(URL location, ResourceBundle resources) {
		AutoBinder ab = new AutoBinder(this);
		event.addListener((observableValue, oldValue, newValue) -> ab.rebindAll());

		nameValidator = TextValidator.create(nameField).fieldName("Name").notEmpty(true).validateOnChange();
		locationValidator = TextValidator.create(locationField).fieldName("Ort").notEmpty(true).validateOnChange();
		eventTypeValidator = new ComboBoxValidator<>(eventTypeComboBox).fieldName("Veranstaltungsart").isSelected(true).validateOnChange();
		toDateValidator = DateValidator.create(toDatePicker).fieldName("Bis-Datum").after(fromDatePicker).relationFieldName("Von-Datum").validateOnChange();

		loadEventType();
	}

	private void loadEventType() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<EventType> result = em.createNamedQuery("EventType.findAll", EventType.class).getResultList();

		eventTypeComboBox.populate(result, null);
	}

	public void addNewEventType(ActionEvent actionEvent) {
		new EventTypeOverviewController().create(actionEvent);

		loadEventType();
	}

	public boolean isValid() {
		boolean nameValid = nameValidator.validate();
		boolean eventTypeValid = eventTypeValidator.validate();
		boolean toDateValid = toDateValidator.validate();
		boolean locationValid = locationValidator.validate();

		return nameValid && eventTypeValid && toDateValid && locationValid;
	}
}

package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.meta.EventTypeOverviewController;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.meta.EventType;
import de.spiritaner.maz.util.database.DataDatabase;
import de.spiritaner.maz.view.renderer.DatePickerFormatter;
import de.spiritaner.maz.view.renderer.MetaClassListCell;
import de.spiritaner.maz.util.validator.ComboBoxValidator;
import de.spiritaner.maz.util.validator.DateValidator;
import de.spiritaner.maz.util.validator.TextValidator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class EventEditorController implements Initializable {

	final static Logger logger = Logger.getLogger(EventEditorController.class);

	@FXML
	private TextField nameField;
	@FXML
	private TextArea descriptionArea;
	@FXML
	private DatePicker fromDatePicker;
	@FXML
	private DatePicker toDatePicker;
	@FXML
	private ComboBox<EventType> eventTypeComboBox;
	@FXML
	private Button addNewEventTypeButton;
	@FXML
	private TextField locationField;

	private TextValidator nameValidator;
	private ComboBoxValidator<EventType> eventTypeValidator;
	private DateValidator toDateValidator;
	private TextValidator locationValidator;

	public void initialize(URL location, ResourceBundle resources) {
		nameValidator = TextValidator.create(nameField).fieldName("Name").notEmpty(true).validateOnChange();
		locationValidator = TextValidator.create(locationField).fieldName("Ort").notEmpty(true).validateOnChange();
		eventTypeValidator = new ComboBoxValidator<>(eventTypeComboBox).fieldName("Veranstaltungsart").isSelected(true).validateOnChange();
		toDateValidator = DateValidator.create(toDatePicker).fieldName("Bis-Datum").after(fromDatePicker).relationFieldName("Von-Datum").validateOnChange();

		fromDatePicker.setConverter(new DatePickerFormatter());
		toDatePicker.setConverter(new DatePickerFormatter());

		eventTypeComboBox.setCellFactory(column -> new MetaClassListCell<>());
		eventTypeComboBox.setButtonCell(new MetaClassListCell<>());

		loadEventType();
	}

	public void setAll(Event event) {
		nameField.setText(event.getName());
		descriptionArea.setText(event.getDescription());
		fromDatePicker.setValue(event.getFromDate());
		toDatePicker.setValue(event.getToDate());
		eventTypeComboBox.setValue(event.getEventType());
		locationField.setText(event.getLocation());
	}

	public Event getAll(Event event) {
		if (event == null) event = new Event();
		event.setName(nameField.getText());
		event.setDescription(descriptionArea.getText());
		event.setFromDate(fromDatePicker.getValue());
		event.setToDate(toDatePicker.getValue());
		event.setEventType(eventTypeComboBox.getValue());
		event.setLocation(locationField.getText());
		return event;
	}

	public void setReadonly(boolean readonly) {
		nameField.setDisable(readonly);
		descriptionArea.setDisable(readonly);
		fromDatePicker.setDisable(readonly);
		toDatePicker.setDisable(readonly);
		toDatePicker.setDisable(readonly);
		eventTypeComboBox.setDisable(readonly);
		addNewEventTypeButton.setDisable(readonly);
		locationField.setDisable(readonly);
	}

	public void loadEventType() {
		EntityManager em = DataDatabase.getFactory().createEntityManager();
		Collection<EventType> result = em.createNamedQuery("EventType.findAll", EventType.class).getResultList();

		EventType selectedBefore = eventTypeComboBox.getValue();
		eventTypeComboBox.getItems().clear();
		eventTypeComboBox.getItems().addAll(FXCollections.observableArrayList(result));
		eventTypeComboBox.setValue(selectedBefore);
	}

	public boolean isValid() {
		boolean nameValid = nameValidator.validate();
		boolean eventTypeValid = eventTypeValidator.validate();
		boolean toDateValid = toDateValidator.validate();
		boolean locationValid = locationValidator.validate();

		return nameValid && eventTypeValid && toDateValid && locationValid;
	}

	public void addNewEventType(ActionEvent actionEvent) {
		new EventTypeOverviewController().create(actionEvent);

		loadEventType();
	}
}

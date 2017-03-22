package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.meta.EventTypeEditorController;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.meta.EventType;
import de.spiritaner.maz.util.DataDatabase;
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

/**
 * Created by Florian on 8/13/2016.
 */
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

	private TextValidator nameFieldValidator;
	private ComboBoxValidator<EventType> eventTypeValidator;
	private DateValidator toDateValidator;

	public void initialize(URL location, ResourceBundle resources) {
		nameFieldValidator = TextValidator.create(nameField).fieldName("Name").notEmpty(true).validateOnChange();
		eventTypeValidator = new ComboBoxValidator<>(eventTypeComboBox).fieldName("Veranstaltungsart").isSelected(true).validateOnChange();
		toDateValidator = DateValidator.create(toDatePicker).fieldName("Bis-Datum").after(fromDatePicker).relationFieldName("Von-Datum").validateOnChange();

		eventTypeComboBox.setCellFactory(column -> {
			return new ListCell<EventType>() {
				@Override
				public void updateItem(EventType item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
					} else {
						setText(item.getDescription());
					}
				}
			};
		});
		eventTypeComboBox.setButtonCell(new ListCell<EventType>() {
			@Override
			protected void updateItem(EventType item, boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setText(null);
				} else {
					setText(item.getDescription());
				}
			}
		});

		loadEventType();
	}

	public void setAll(Event event) {
		nameField.setText(event.getName());
		descriptionArea.setText(event.getDescription());
		fromDatePicker.setValue(event.getFromDate());
		toDatePicker.setValue(event.getToDate());
		eventTypeComboBox.setValue(event.getEventType());
	}

	public Event getAll(Event event) {
		if (event == null) event = new Event();
		event.setName(nameField.getText());
		event.setDescription(descriptionArea.getText());
		event.setFromDate(fromDatePicker.getValue());
		event.setToDate(toDatePicker.getValue());
		event.setEventType(eventTypeComboBox.getValue());
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
		boolean nameValid = nameFieldValidator.validate();
		boolean eventTypeValid = eventTypeValidator.validate();
		boolean toDateValid = toDateValidator.validate();

		return nameValid && eventTypeValid && toDateValid;
	}

	public void addNewEventType(ActionEvent actionEvent) {
		new EventTypeEditorController().create(actionEvent);

		loadEventType();
	}
}

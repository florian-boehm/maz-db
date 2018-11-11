package de.spiritaner.maz.controller.participation;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.meta.EventTypeOverviewController;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.meta.EventType;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.*;
import de.spiritaner.maz.view.binding.BindableProperty;
import de.spiritaner.maz.view.component.BindableComboBox;
import de.spiritaner.maz.view.component.BindableDatePicker;
import de.spiritaner.maz.view.component.BindableTextArea;
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

public class EventEditorController extends EditorController {

	@BindableProperty
	public ObjectProperty<Event> event = new SimpleObjectProperty<>();

	public BindableTextField nameField;
	public BindableTextArea descriptionArea;
	public BindableDatePicker fromDatePicker;
	public BindableDatePicker toDatePicker;
	public BindableComboBox<EventType> eventTypeComboBox;
	public BindableTextField locationField;
	public Button addNewEventTypeButton;

	public void initialize(URL location, ResourceBundle resources) {
		// Bind all bindable fields to the bindable property
		autoBinder.register(this);
		event.addListener((observableValue, oldValue, newValue) -> autoBinder.rebindAll());

		// Change the validator visitor to PopOver and add Validations as well as change listeners
		autoValidator.visitor = new PopOverVisitor();
		autoValidator.add(new NotEmpty(nameField, guiText.getString("name")));
		autoValidator.add(new NotEmpty(locationField, guiText.getString("location")));
		autoValidator.add(new Selected(eventTypeComboBox, guiText.getString("event_type")));
		autoValidator.add(new NotEmpty(fromDatePicker, guiText.getString("from_date")));
		autoValidator.add(new NotEmpty(toDatePicker, guiText.getString("to_date")));
		autoValidator.add(new After(toDatePicker, guiText.getString("to_date"), fromDatePicker, guiText.getString("from_date"), true));
		autoValidator.validateOnChange(nameField);
		autoValidator.validateOnChange(locationField);
		autoValidator.validateOnChange(eventTypeComboBox);
		autoValidator.validateOnChange(fromDatePicker);
		autoValidator.validateOnChange(toDatePicker);

		// Custom format of some fields
		toDatePicker.setConverter(new DatePickerFormatter());

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
}

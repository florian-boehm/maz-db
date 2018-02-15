package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.participation.EventEditorDialogController;
import de.spiritaner.maz.model.meta.EventType;
import javafx.beans.property.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Florian Schwab
 */
@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = EventEditorDialogController.class, identifiableName = "Veranstaltung")
@NamedQueries({
		  @NamedQuery(name = "Event.findAll", query = "SELECT e FROM Event e"),
})
public class Event implements Identifiable {

	private LongProperty id;

	private StringProperty name;
	private StringProperty description;

	private ObjectProperty<LocalDate> fromDate;
	private ObjectProperty<LocalDate> toDate;

	private List<Participation> participations;
	private ObjectProperty<EventType> eventType;
	private StringProperty location;

	public Event() {
		id = new SimpleLongProperty();
		name = new SimpleStringProperty();
		description = new SimpleStringProperty();
		fromDate = new SimpleObjectProperty<>();
		toDate = new SimpleObjectProperty<>();
		location = new SimpleStringProperty();
		participations = new ArrayList<>();
		eventType = new SimpleObjectProperty<>();
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id.get();
	}

	public LongProperty idProperty() {
		return id;
	}

	public void setId(long id) {
		this.id.set(id);
	}

	@Column(nullable = false)
	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getDescription() {
		return description.get();
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	public LocalDate getFromDate() {
		return fromDate.get();
	}

	public ObjectProperty<LocalDate> fromDateProperty() {
		return fromDate;
	}

	public void setFromDate(LocalDate fromDate) {
		this.fromDate.set(fromDate);
	}

	public LocalDate getToDate() {
		return toDate.get();
	}

	public ObjectProperty<LocalDate> toDateProperty() {
		return toDate;
	}

	public void setToDate(LocalDate toDate) {
		this.toDate.set(toDate);
	}

	@OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
	@Cascade(CascadeType.DELETE)
	public List<Participation> getParticipations() {
		return participations;
	}

	public void setParticipations(List<Participation> participations) {
		this.participations = participations;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "eventTypeId", nullable = false)
	public EventType getEventType() {
		return eventType.get();
	}

	public void setEventType(EventType eventType) {
		this.eventType.set(eventType);
	}

	public ObjectProperty<EventType> eventTypeProperty() {
		return eventType;
	}

	@Column(nullable = false)
	public String getLocation() {
		return location.get();
	}

	public StringProperty locationProperty() {
		return location;
	}

	public void setLocation(String location) {
		this.location.set(location);
	}

	@Override
	@Transient
	public boolean equals(Object obj) {
		return (obj instanceof Event) && (((Event) obj).getId().equals(this.getId()));
	}

	@Override
	@Transient
	public String toString() {
		return name.get() + ((fromDate.get() == null) ? "" : " (" + fromDate.get().getYear() + ")");
	}
}

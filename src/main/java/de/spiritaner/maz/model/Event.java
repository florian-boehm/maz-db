package de.spiritaner.maz.model;

import javafx.beans.property.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
public class Event {

	private LongProperty id;

	private StringProperty name;
	private StringProperty description;

	private ObjectProperty<LocalDate> fromDate;
	private ObjectProperty<LocalDate> toDate;

   private List<Participant> participants;
	private EventType eventType;
	private Address address;

	public Event() {
		id = new SimpleLongProperty();
		name = new SimpleStringProperty();
		description = new SimpleStringProperty();
		fromDate = new SimpleObjectProperty<>();
		toDate = new SimpleObjectProperty<>();
	}

	@Id
	@GeneratedValue
	public long getId() {
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
	public List<Participant> getParticipants() {
		return participants;
	}
	public void setParticipants(List<Participant> participants) {
		this.participants = participants;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "eventTypeId", nullable = false)
	public EventType getEventType() { return eventType; }
	public void setEventType(EventType eventType) { this.eventType = eventType; }

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="addressId", nullable = false)
	public Address getAddress() { return address; }
	public void setAddress(Address address) { this.address = address; }
}

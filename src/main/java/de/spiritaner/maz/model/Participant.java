package de.spiritaner.maz.model;

import de.spiritaner.maz.model.meta.ParticipantType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@Audited
public class Participant implements Identifiable {

	LongProperty id;

	private Person person;
	private Event event;
	private ParticipantType participantType;

	private BooleanProperty hasParticipated;

	public Participant() {
		id = new SimpleLongProperty();
		hasParticipated = new SimpleBooleanProperty();
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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="eventId", nullable = false)
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="personId", nullable = false)
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "participantTypeId", nullable = false)
	public ParticipantType getParticipantType() { return participantType; }
	public void setParticipantType(ParticipantType type) { this.participantType = type; }

	@Column(nullable = false)
	public Boolean getHasParticipated() { return hasParticipated.get(); }
	public void setHasParticipated(Boolean hasParticipated) { this.hasParticipated.set(hasParticipated); }
	public BooleanProperty hasParticipatedProperty() { return hasParticipated; }
}

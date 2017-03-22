package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.participation.ParticipationEditorDialogController;
import de.spiritaner.maz.model.meta.ParticipationType;
import javafx.beans.property.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = ParticipationEditorDialogController.class, identifiableName = "Teilnahme")
public class Participation implements Identifiable {

	LongProperty id;

	private Person person;
	private Event event;
	private ParticipationType participationType;

	private BooleanProperty hasParticipated;

	public Participation() {
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
	@JoinColumn(name="personId", nullable = false)
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="eventId", nullable = false)
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "participantTypeId", nullable = false)
	public ParticipationType getParticipationType() { return participationType; }
	public void setParticipationType(ParticipationType type) { this.participationType = type; }

	@Column(nullable = false)
	public Boolean getHasParticipated() { return hasParticipated.get(); }
	public void setHasParticipated(Boolean hasParticipated) { this.hasParticipated.set(hasParticipated); }
	public BooleanProperty hasParticipatedProperty() { return hasParticipated; }

	@Transient
	public StringProperty hasParticipatedStringProperty() {
		return new SimpleStringProperty((hasParticipated.get()) ? "Ja" : "Nein");
	}

	@Transient
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Participation) && (((Participation) obj).getId().equals(getId()));
	}
}

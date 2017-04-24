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

	private LongProperty id;

	private ObjectProperty<Person> person;
	private ObjectProperty<Event> event;
	private ObjectProperty<ParticipationType> participationType;

	private BooleanProperty hasParticipated;

	public Participation() {
		id = new SimpleLongProperty();
		hasParticipated = new SimpleBooleanProperty();
		person = new SimpleObjectProperty<>();
		event = new SimpleObjectProperty<>();
		participationType = new SimpleObjectProperty<>();
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
		return person.get();
	}
	public void setPerson(Person person) {
		this.person.set(person);
	}
	public ObjectProperty<Person> personProperty() {
		return person;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="eventId", nullable = false)
	public Event getEvent() {
		return event.get();
	}
	public void setEvent(Event event) {
		this.event.set(event);
	}
	public ObjectProperty<Event> eventProperty() {
		return event;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "participantTypeId", nullable = false)
	public ParticipationType getParticipationType() { return participationType.get(); }
	public void setParticipationType(ParticipationType type) { this.participationType.set(type); }
	public ObjectProperty<ParticipationType> participationTypeProperty() {
		return participationType;
	}

	@Column(nullable = false)
	public Boolean getHasParticipated() { return hasParticipated.get(); }
	public void setHasParticipated(Boolean hasParticipated) { this.hasParticipated.set(hasParticipated); }
	public BooleanProperty hasParticipatedProperty() { return hasParticipated; }

	@Transient
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Participation) && (((Participation) obj).getId().equals(getId()));
	}
}

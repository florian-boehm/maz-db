package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.relationship.RelationshipEditorDialogController;
import de.spiritaner.maz.model.meta.RelationshipType;
import javafx.beans.property.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = RelationshipEditorDialogController.class, identifiableName = "$relationship")
@NamedQueries({
		  @NamedQuery(name = "Relationship.findAll", query = "SELECT r FROM Relationship r"),
})
public class Relationship implements Identifiable {

	public LongProperty id = new SimpleLongProperty();
	public ObjectProperty<RelationshipType> relationshipType = new SimpleObjectProperty<>();
	public ObjectProperty<Person> fromPerson;
	public ObjectProperty<Person> toPerson;
	public StringProperty toPersonFirstName = new SimpleStringProperty();
	public StringProperty toPersonFamilyName = new SimpleStringProperty();

	@Id
	@GeneratedValue
	@Override
	public Long getId() {
		return id.get();
	}
	public void setId(long id) {
		this.id.set(id);
	}
	public LongProperty idProperty() {
		return id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "relationshipTypeId", nullable = false)
	public RelationshipType getRelationshipType() {
		return relationshipType.get();
	}
	public void setRelationshipType(RelationshipType relationshipType) {
		this.relationshipType.set(relationshipType);
	}
	public ObjectProperty<RelationshipType> relationshipTypeProperty() { return relationshipType; }


	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fromPersonId", nullable = false)
	public Person getFromPerson() {
		return fromPerson.get();
	}
	public void setFromPerson(Person person) {
		this.fromPerson.set(person);
	}
	public ObjectProperty<Person> fromPersonProperty() {
		return fromPerson;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "toPersonId", nullable = true)
	public Person getToPerson() {
		return toPerson.get();
	}
	public void setToPerson(Person person) {
		this.toPerson.set(person);
	}
	public ObjectProperty<Person> toPersonProperty() {
		return toPerson;
	}

	/**
	 * If the person does not exist as explicit Person entity his/her first editable can be inserted here manually
	 *
	 * @return
	 */
	public String getToPersonFirstName() {
		return toPersonFirstName.get();
	}
	public void setToPersonFirstName(String toPersonFirstName) {
		this.toPersonFirstName.set(toPersonFirstName);
	}
	public StringProperty toPersonFirstNameProperty() {
		return toPersonFirstName;
	}

	/**
	 * If the person does not exist as explicit Person entity his/her family editable can be inserted here manually
	 *
	 * @return
	 */
	public String getToPersonFamilyName() {
		return toPersonFamilyName.get();
	}
	public void setToPersonFamilyName(String toPersonFamilyName) {
		this.toPersonFamilyName.set(toPersonFamilyName);
	}
	public StringProperty toPersonFamilyNameProperty() {
		return toPersonFamilyName;
	}

	@Transient
	@Override
	public boolean equals(Object object) {
		return (object instanceof Relationship) && ((Relationship) object).getId().equals(getId());
	}

	@Transient
	public String getToPersonFullName() {
		return (toPerson != null) ? toPerson.get().getFullName() : toPersonFirstName.get() + " " + toPersonFamilyName.get();
	}
}

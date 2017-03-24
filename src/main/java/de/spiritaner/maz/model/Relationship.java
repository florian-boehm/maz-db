package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.relationship.RelationshipEditorDialogController;
import de.spiritaner.maz.model.meta.RelationshipType;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = RelationshipEditorDialogController.class, identifiableName = "Beziehung")
@NamedQueries({
		  @NamedQuery(name = "Relationship.findAll", query = "SELECT r FROM Relationship r"),
})
public class Relationship implements Identifiable {

	private LongProperty id;
	private RelationshipType relationshipType;
	private Person fromPerson;
	private Person toPerson;
	private StringProperty toPersonFirstName;
	private StringProperty toPersonFamilyName;

	public Relationship() {
		id = new SimpleLongProperty();
		toPersonFirstName = new SimpleStringProperty();
		toPersonFamilyName = new SimpleStringProperty();
	}

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
		return relationshipType;
	}

	public void setRelationshipType(RelationshipType relationshipType) {
		this.relationshipType = relationshipType;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fromPersonId", nullable = false)
	public Person getFromPerson() {
		return fromPerson;
	}

	public void setFromPerson(Person person) {
		this.fromPerson = person;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "toPersonId", nullable = true)
	public Person getToPerson() {
		return toPerson;
	}

	public void setToPerson(Person person) {
		this.toPerson = person;
	}

	/**
	 * If the person does not exist as explicit Person entity his/her first name can be inserted here manually
	 *
	 * @return
	 */
	public String getToPersonFirstName() {
		return toPersonFirstName.get();
	}

	public StringProperty toPersonFirstNameProperty() {
		return toPersonFirstName;
	}

	public void setToPersonFirstName(String toPersonFirstName) {
		this.toPersonFirstName.set(toPersonFirstName);
	}

	/**
	 * If the person does not exist as explicit Person entity his/her family name can be inserted here manually
	 *
	 * @return
	 */
	public String getToPersonFamilyName() {
		return toPersonFamilyName.get();
	}

	public StringProperty toPersonFamilyNameProperty() {
		return toPersonFamilyName;
	}

	public void setToPersonFamilyName(String toPersonFamilyName) {
		this.toPersonFamilyName.set(toPersonFamilyName);
	}

	@Transient
	@Override
	public boolean equals(Object object) {
		return (object instanceof Relationship) && ((Relationship) object).getId().equals(getId());
	}

	@Transient
	public String getToPersonFullName() {
		return (toPerson != null) ? toPerson.getFullName() : toPersonFirstName.get() + " " + toPersonFamilyName.get();
	}
}

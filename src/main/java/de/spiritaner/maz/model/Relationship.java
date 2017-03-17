package de.spiritaner.maz.model;

import de.spiritaner.maz.model.meta.RelationshipType;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by Florian on 3/14/2017.
 */
@Entity
@Audited
@NamedQueries({
		  @NamedQuery(name = "Relationship.findAll", query = "SELECT r FROM Relationship r"),
})
public class Relationship implements Identifiable {

	private LongProperty id;
	private RelationshipType relationshipType;
	private Person fromPerson;
	private Person toPerson;
	private StringProperty toPersonName;

	public Relationship() {
		id = new SimpleLongProperty();
		toPersonName = new SimpleStringProperty();
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
	@JoinColumn(name = "getPersonId", nullable = true)
	public Person getToPerson() {
		return toPerson;
	}

	public void setToPerson(Person person) {
		this.toPerson = person;
	}

	public String getToPersonName() {
		return toPersonName.get();
	}

	public StringProperty toPersonNameProperty() {
		return toPersonName;
	}

	public void setToPersonName(String toPersonName) {
		this.toPersonName.set(toPersonName);
	}
}

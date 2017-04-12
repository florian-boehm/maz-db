package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.participation.ParticipationEditorDialogController;
import de.spiritaner.maz.controller.residence.ResidenceEditorDialogController;
import de.spiritaner.maz.model.meta.ResidenceType;
import de.spiritaner.maz.util.DataDatabase;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.xml.bind.SchemaOutputResolver;
import java.util.ArrayList;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = ResidenceEditorDialogController.class, identifiableName = "Wohnort")
@NamedQueries({
		  @NamedQuery(name = "Residence.findAll", query = "SELECT r FROM Residence r"),
		  @NamedQuery(name = "Residence.findAllForPerson", query = "SELECT r FROM Residence r WHERE r.person=:person")
})
public class Residence implements Identifiable {

	private LongProperty id;
	private Person person;
	private Address address;
	private ResidenceType residenceType;

	public Residence() {
		id = new SimpleLongProperty();
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id.get();
	}

	public void setId(Long id) {
		this.id.set(id);
	}

	public LongProperty idProperty() {
		return id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "personId", nullable = false)
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "addressId", nullable = false)
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "residenceTypeId", nullable = false)
	public ResidenceType getResidenceType() {
		return residenceType;
	}

	public void setResidenceType(ResidenceType residenceType) {
		this.residenceType = residenceType;
	}

	@Transient
	public Boolean getPreferredAddress() {
		return (getPerson() != null && getPerson().getPreferredResidence() != null) ? getPerson().getPreferredResidence().equals(this) : Boolean.FALSE;
	}

	@Transient
	public StringProperty preferredAddressProperty() {
		return new SimpleStringProperty((getPreferredAddress()) ? "Ja" : "Nein");
	}

	@Override
	@Transient
	public boolean equals(Object obj) {
		return (obj instanceof Residence) && (((Residence) obj).getId().equals(this.getId()));
	}
}

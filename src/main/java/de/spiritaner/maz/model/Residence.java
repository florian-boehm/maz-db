package de.spiritaner.maz.model;

import de.spiritaner.maz.model.meta.ResidenceType;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@Audited
@NamedQueries({
		@NamedQuery(name = "Residence.findAll", query = "SELECT r FROM Residence r"),
		@NamedQuery(name = "Residence.findAllForPerson", query = "SELECT r FROM Residence r WHERE r.person=:person")
})
public class Residence {

	private LongProperty id;
	private Person person;
	private Address address;
	private ResidenceType residenceType;
	private Boolean isPreferredAddress;

	public Residence() {
	    id = new SimpleLongProperty();
    }

	@Id
	@GeneratedValue
	public Long getId() {
		return id.get();
	}
	public void setId(Long id) { this.id.set(id); }
	public LongProperty idProperty() { return id; }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="personId", nullable = false)
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST })
    @JoinColumn(name="addressId", nullable = false)
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

	public Boolean getPreferredAddress() {
		return (isPreferredAddress == null) ? Boolean.FALSE : isPreferredAddress;
	}
	public void setPreferredAddress(Boolean preferredAddress) {
		isPreferredAddress = preferredAddress;
	}
	public StringProperty preferredAddressProperty() {
	    return new SimpleStringProperty((isPreferredAddress) ? "Ja" : "Nein");
    }
}

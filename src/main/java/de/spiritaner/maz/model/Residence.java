package de.spiritaner.maz.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
public class Residence {

	@Id
	@GeneratedValue
	private Long id;
//	@EmbeddedId
//	private TemporalEntityKey id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="personId", nullable = false)
	private Person person;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="addressId", nullable = false)
	private Address address;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "residenceTypeId", nullable = false)
	private ResidenceType residenceType;

	private Boolean isPreferredAddress;

//	public TemporalEntityKey getId() {
//		return id;
//	}
//
//	public void setId(TemporalEntityKey id) {
//		this.id = id;
//	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public ResidenceType getResidenceType() {
		return residenceType;
	}

	public void setResidenceType(ResidenceType residenceType) {
		this.residenceType = residenceType;
	}

	public Boolean getPreferredAddress() {
		return isPreferredAddress;
	}

	public void setPreferredAddress(Boolean preferredAddress) {
		isPreferredAddress = preferredAddress;
	}
}

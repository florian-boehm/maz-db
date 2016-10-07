package de.spiritaner.maz.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
public class Address {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String street;

	@Column(nullable = false)
	private String houseNumber;

	@Column(nullable = false, length = 10)
	private String postCode;

	@Column(nullable = false)
	private String city;

	@Column(nullable = true)
	private String state;

	@Column(nullable = true)
	private String country;

	private String addition;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * The state is not necessary for delivery of post in Germany
	 *
	 * @return The state of this address; might be null
	 */
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	/**
	 * The country is not necessary for delivery of post in Germany
	 *
	 * @return The country of this address; might be null
	 */
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAddition() {
		return addition;
	}

	public void setAddition(String addition) {
		this.addition = addition;
	}
}

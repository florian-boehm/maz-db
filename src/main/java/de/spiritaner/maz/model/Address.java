package de.spiritaner.maz.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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

	private LongProperty id;
	private StringProperty street;
	private StringProperty houseNumber;
	private StringProperty postCode;
	private StringProperty city;
	private StringProperty state;
	private StringProperty country;
	private StringProperty addition;

	public Address() {
		id = new SimpleLongProperty();
		street = new SimpleStringProperty();
		houseNumber = new SimpleStringProperty();
		postCode = new SimpleStringProperty();
		city = new SimpleStringProperty();
		state = new SimpleStringProperty();
		country = new SimpleStringProperty();
		addition = new SimpleStringProperty();
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id.get();
	}
	public void setId(Long id) {
		this.id.set(id);
	}
	public LongProperty idProperty() { return id; }

	@Column(nullable = false)
	public String getStreet() {
		return street.get();
	}
	public void setStreet(String street) {
		this.street.set(street);
	}
	public StringProperty streetProperty() { return street; }

	@Column(nullable = false)
	public String getHouseNumber() {
		return houseNumber.get();
	}
	public void setHouseNumber(String houseNumber) {
		this.houseNumber.set(houseNumber);
	}
	public StringProperty houseNumberProperty() { return this.houseNumber; }

	@Column(nullable = false, length = 10)
	public String getPostCode() {
		return postCode.get();
	}
	public void setPostCode(String postCode) {
		this.postCode.set(postCode);
	}
	public StringProperty postCodeProperty() { return this.postCode; }

	@Column(nullable = false)
	public String getCity() {
		return city.get();
	}
	public void setCity(String city) {
		this.city.set(city);
	}
	public StringProperty cityProperty() { return city; }

	/**
	 * The state is not necessary for delivery of post in Germany
	 *
	 * @return The state of this address; might be null
	 */
	@Column(nullable = true)
	public String getState() {
		return state.get();
	}
	public void setState(String state) {
		this.state.set(state);
	}
	public StringProperty stateProperty() { return this.state; }

	/**
	 * The country is not necessary for delivery of post in Germany
	 *
	 * @return The country of this address; might be null
	 */
	@Column(nullable = true)
	public String getCountry() {
		return country.get();
	}
	public void setCountry(String country) {
		this.country.set(country);
	}
	public StringProperty countryProperty() { return country; }

	public String getAddition() {
		return addition.get();
	}
	public void setAddition(String addition) {
		this.addition.set(addition);
	}
	public StringProperty additionProperty() { return addition; }
}

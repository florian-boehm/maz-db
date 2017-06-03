package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.residence.AddressEditorDialogController;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = AddressEditorDialogController.class, identifiableName = "Adresse")
@NamedQueries({
		  @NamedQuery(name="Address.findSame", query="SELECT a FROM Address a WHERE a.street=:street AND " +
					 "a.houseNumber=:houseNumber AND a.postCode=:postCode AND a.city=:city AND a.state=:state AND " +
					 "a.country=:country AND a.addition=:addition")
})
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"street", "houseNumber", "postCode", "city", "state", "country", "addition"}))
public class Address implements Identifiable {

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
	 * @return The state of this residence; might be null
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
	 * @return The country of this residence; might be null
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

	public static Address findSame(EntityManager em, Address address) {
		TypedQuery<Address> query = em.createNamedQuery("Address.findSame", Address.class);
		query.setParameter("street", address.getStreet());
		query.setParameter("houseNumber", address.getHouseNumber());
		query.setParameter("postCode", address.getPostCode());
		query.setParameter("city", address.getCity());
		query.setParameter("state", address.getState());
		query.setParameter("country", address.getCountry());
		query.setParameter("addition", address.getAddition());
		ArrayList<Address> results = (ArrayList<Address>) query.getResultList();

		return (results.size() == 1) ? em.merge(results.get(0)) : address;
	}

	@Transient
	@Override
	public String toString() {
		return street.get() + " " + houseNumber.get() + ", " + postCode.get() + " "  + city.get();
	}
}

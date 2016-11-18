package de.spiritaner.maz.model;


import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;
import java.util.List;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
public class ContactMethodType {

	private LongProperty id;
	private StringProperty description;
	private List<ContactMethod> contactMethods;

	public ContactMethodType() {
		id = new SimpleLongProperty();
		description = new SimpleStringProperty();
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

	/**
	 * The description of the contact method type.
	 */
	@Column(nullable = false)
	public String getDescription() {
		return description.get();
	}
	public void setDescription(String description) {
		this.description.set(description);
	}
	public StringProperty descriptionProperty() { return description; }

	/**
	 * All contact methods that use this contact method type.
	 */
	@OneToMany(mappedBy = "contactMethodType", fetch = FetchType.LAZY)
	public List<ContactMethod> getContactMethods() { return contactMethods; }
	public void setContactMethods(List<ContactMethod> contactMethods) { this.contactMethods = contactMethods; }
}

package de.spiritaner.maz.model;

import de.spiritaner.maz.model.meta.ContactMethodType;
import javafx.beans.property.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@Audited
@NamedQueries({
		@NamedQuery(name = "ContactMethod.findAll", query = "SELECT cm FROM ContactMethod cm"),
		@NamedQuery(name = "ContactMethod.findAllForPerson", query = "SELECT cm FROM ContactMethod cm WHERE cm.person=:person")
})
public class ContactMethod implements Identifiable  {

	private LongProperty id;
	private StringProperty value;

	private Person person;
	private ContactMethodType contactMethodType;

	private BooleanProperty preferred;

	public ContactMethod() {
	    id = new SimpleLongProperty();
	    value = new SimpleStringProperty();
	    preferred = new SimpleBooleanProperty();
	    preferred.set(false);
    }

	@Id
	@GeneratedValue
	public Long getId() {
		return id.get();
	}
	public LongProperty idProperty() {
		return id;
	}
	public void setId(long id) {
		this.id.set(id);
	}

	@Column(nullable = false)
	public String getValue() {
		return value.get();
	}
	public StringProperty valueProperty() {
		return value;
	}
	public void setValue(String value) {
		this.value.set(value);
	}

	/**
	 * The person that can be reached by this contact method.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="personId", nullable = false)
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * The contact method type that this contact method is using.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "contactMethodTypeId", nullable = false)
	public ContactMethodType getContactMethodType() {
		return contactMethodType;
	}
	public void setContactMethodType(ContactMethodType contactMethodType) {
		this.contactMethodType = contactMethodType;
	}

	public boolean isPreferred() {
		return preferred.get();
	}
	public BooleanProperty preferredProperty() {
		return preferred;
	}
	public void setPreferred(boolean preferred) {
		this.preferred.set(preferred);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ContactMethod) && (this.getId().equals(((ContactMethod) obj).getId()));
	}
}

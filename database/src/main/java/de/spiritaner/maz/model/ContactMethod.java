package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.contactmethod.ContactMethodEditorDialogController;
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
@Identifiable.Annotation(identifiableName = "Kontaktweg", editorDialogClass = ContactMethodEditorDialogController.class)
@NamedQueries({
		  @NamedQuery(name = "ContactMethod.findAll", query = "SELECT cm FROM ContactMethod cm"),
		  @NamedQuery(name = "ContactMethod.findAllForPerson", query = "SELECT cm FROM ContactMethod cm WHERE cm.person=:person")
})
public class ContactMethod implements Identifiable {

	public LongProperty id = new SimpleLongProperty();
	public StringProperty value = new SimpleStringProperty();

	public ObjectProperty<Person> person = new SimpleObjectProperty<>();
	public ObjectProperty<ContactMethodType> contactMethodType = new SimpleObjectProperty<>();
	public StringProperty remark = new SimpleStringProperty();

	public BooleanProperty preferred = new SimpleBooleanProperty(false);

	@Id
	@GeneratedValue
	public Long getId() {
		return id.get();
	}

	public void setId(long id) {
		this.id.set(id);
	}

	public LongProperty idProperty() {
		return id;
	}

	@Column(nullable = false)
	public String getValue() {
		return value.get();
	}

	public void setValue(String value) {
		this.value.set(value);
	}

	public StringProperty valueProperty() {
		return value;
	}

	/**
	 * The person that can be reached by this contact method.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "personId", nullable = false)
	public Person getPerson() {
		return person.get();
	}

	public void setPerson(Person person) {
		this.person.set(person);
	}

	public ObjectProperty<Person> personProperty() {
		return person;
	}

	/**
	 * The contact method type that this contact method is using.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "contactMethodTypeId", nullable = false)
	public ContactMethodType getContactMethodType() {
		return contactMethodType.get();
	}

	public void setContactMethodType(ContactMethodType contactMethodType) {
		this.contactMethodType.set(contactMethodType);
	}

	public ObjectProperty<ContactMethodType> contactMethodTypeProperty() {
		return contactMethodType;
	}

	public boolean isPreferred() {
		return preferred.get();
	}

	public void setPreferred(boolean preferred) {
		this.preferred.set(preferred);
	}

	public BooleanProperty preferredProperty() {
		return preferred;
	}

	public String getRemark() {
		return remark.get();
	}

	public StringProperty remarkProperty() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark.set(remark);
	}

	@Override
	@Transient
	public boolean equals(Object obj) {
		return (obj instanceof ContactMethod) && (this.getId().equals(((ContactMethod) obj).getId()));
	}
}

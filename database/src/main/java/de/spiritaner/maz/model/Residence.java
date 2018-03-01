package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.residence.ResidenceEditorDialogController;
import de.spiritaner.maz.model.meta.ResidenceType;
import javafx.beans.property.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = ResidenceEditorDialogController.class, identifiableName = "$residence")
@NamedQueries({
		  @NamedQuery(name = "Residence.findAll", query = "SELECT r FROM Residence r"),
		  @NamedQuery(name = "Residence.findAllForPerson", query = "SELECT r FROM Residence r WHERE r.person=:person"),
		  @NamedQuery(name = "Residence.findPostAddressForPerson", query = "SELECT r FROM Residence r WHERE r.person=:person AND r.forPost=TRUE")
})
public class Residence implements Identifiable {

	public LongProperty id = new SimpleLongProperty();
	public ObjectProperty<Person> person = new SimpleObjectProperty<>();
	public ObjectProperty<Address> address = new SimpleObjectProperty<>();
	public ObjectProperty<ResidenceType> residenceType = new SimpleObjectProperty<>();
	public BooleanProperty forPost = new SimpleBooleanProperty(Boolean.FALSE);

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
		return person.get();
	}
	public void setPerson(Person person) {
		this.person.set(person);
	}
	public ObjectProperty<Person> personProperty() {
		return person;
	}

	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "addressId", nullable = false)
	public Address getAddress() {
		return address.get();
	}
	public void setAddress(Address address) {
		this.address.set(address);
	}
	public ObjectProperty<Address> addressProperty() {
		return address;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "residenceTypeId", nullable = false)
	public ResidenceType getResidenceType() {
		return residenceType.get();
	}
	public void setResidenceType(ResidenceType residenceType) {
		this.residenceType.set(residenceType);
	}
	public ObjectProperty<ResidenceType> residenceTypeProperty() {
		return residenceType;
	}

	@Column(nullable = false)
	public boolean isForPost() {
		return forPost.get();
	}
	public BooleanProperty forPostProperty() {
		return forPost;
	}
	public void setForPost(boolean forPost) {
		this.forPost.set(forPost);
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

package de.spiritaner.maz.model;

import javafx.beans.property.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Florian Schwab
 * @version 0.0.2
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p"),
	//@NamedQuery(name="Person.fetchAllResidences",query="SELECT r FROM Residence where personId=:personId"),
})
public class Person {

	private LongProperty id;

	private Salutation salutation;
	private StringProperty honorific;

	private StringProperty firstName;
	private StringProperty familyName;
	private StringProperty birthName;

	private ObjectProperty<LocalDate> birthday;
	private StringProperty birthplace;

	private Gender gender;
	private List<Residence> residences;
	private Address diocese;
	private List<Role> roles;
	private List<Approval> approvals;

    private List<ContactMethod> contactMethods;

	public Person() {
		id = new SimpleLongProperty();
		firstName = new SimpleStringProperty();
		familyName = new SimpleStringProperty();
		honorific = new SimpleStringProperty();
		birthday = new SimpleObjectProperty<>();
		birthplace = new SimpleStringProperty();
		birthName = new SimpleStringProperty();
	}

	/**
	 * Every person has a composite key to track changes made to the dataset.
	 * The surrogate key of the TemporalEntityKey will be persistent until the person is deleted.
	 * The natural key of the TemporalEntityKey represents the time when the object was changed.
	 *
	 * @return The composite key of a person
	 */
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
	 * The salutation of a person is used for the german T-V distinction.
	 * This specific aspect reflects the level of politeness.
	 *
	 * @return The salutation of a person
	 */
	@ManyToOne(fetch=FetchType.EAGER, cascade = {CascadeType.PERSIST})
	@JoinColumn(name="salutationId")
	public Salutation getSalutation() {
		return salutation;
	}
	public void setSalutation(Salutation salutation) {
		this.salutation = salutation;
	}

	/**
	 * The persons honorific can be e.g. his/her doctor's degree or any title that needs to be mentioned.
	 *
	 * @return The honorific of a person
	 */
	public String getHonorific() {
		return honorific.get();
	}
	public void setHonorific(String honorific) {
		this.honorific.set(honorific);
	}
	public StringProperty honorificProperty() {
		return honorific;
	}

	/**
	 * @return The persons first name.
	 */
	@Column(nullable = false)
	public String getFirstName() {
		return firstName.get();
	}
	public void setFirstName(String firstName) {
		this.firstName.set(firstName);
	}
	public StringProperty firstNameProperty() {
		return firstName;
	}

	/**
	 * The persons family name. When this person marries its family name may be overridden.
	 * The original family name will then become the birth name.
	 *
	 * @return The persons family name
	 */
	@Column(nullable = false)
	public String getFamilyName() {
		return familyName.get();
	}
	public void setFamilyName(String familyName) {
		this.familyName.set(familyName);
	}
	public StringProperty familyNameProperty() {
		return familyName;
	}

	/**
	 * The persons birth name. It is empty until the person marries another person.
	 *
	 * @return The persons birth name, may be empty
	 */
	public String getBirthName() {
		return birthName.get();
	}
	public void setBirthName(String birthName) {
		this.birthName.set(birthName);
	}
	public StringProperty birthNameProperty() {
		return birthName;
	}

	/**
	 * @return The birthday of a person
	 */
	@Column(nullable = false)
	//@Temporal(TemporalType.TIMESTAMP)
	public LocalDate getBirthday() {
		return birthday.get();
	}
	public void setBirthday(LocalDate birthday) {
		this.birthday.set(birthday);
	}
	public ObjectProperty<LocalDate> birthdayProperty() {
		return birthday;
	}

	/**
	 * @return The birthplace of a person, may be empty
	 */
	public String getBirthplace() {
		return birthplace.get();
	}
	public void setBirthplace(String birthplace) {
		this.birthplace.set(birthplace);
	}
	public StringProperty birthplaceProperty() {
		return birthplace;
	}

	/**
	 * One person can only have one gender at a time. If the person has a specific (maybe mixed) gender,
	 * the gender can be created first and then be assigned to the person.
	 *
	 * @return The gender of the person
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="genderId")
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}

	/**
	 * A list of all places associated with this person.
	 * The list will be fetched lazy when it is needed.
	 */
	@OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
	public List<Residence> getResidences() {
		return residences;
	}
	public void setResidences(List<Residence> residences) {
		this.residences = residences;
	}

	/**
	 * The address of the diocese that is responsable for this person.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="dioceseAddress")
	public Address getDiocese() {
		return diocese;
	}
	public void setDiocese(Address diocese) {
		this.diocese = diocese;
	}

	/**
	 * All the roles this specific person has.
	 */
	@OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
	public List<Role> getRoles() { return roles; }
	public void setRoles(List<Role> roles) { this.roles = roles; }

	/**
	 * All the approvals this specific person has.
	 */
	@OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
	public List<Approval> getApprovals() { return approvals; }
	public void setApprovals(List<Approval> approvals) { this.approvals = approvals; }

    /**
     * The contact methods by which this person can be contacted with.
     */
    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    public List<ContactMethod> getContactMethods() {
        return contactMethods;
    }
    public void setContactMethods(List<ContactMethod> contactMethods) {
        this.contactMethods = contactMethods;
    }
}

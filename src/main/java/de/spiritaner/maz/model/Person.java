package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.person.PersonEditorDialogController;
import de.spiritaner.maz.model.meta.Diocese;
import de.spiritaner.maz.model.meta.Gender;
import de.spiritaner.maz.model.meta.Salutation;
import javafx.beans.property.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author Florian Schwab
 * @version 0.0.2
 */
@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = PersonEditorDialogController.class, identifiableName = "Person")
@Indexed
@NamedQueries({
	@NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p"),
	//@NamedQuery(name="Person.fetchAllResidences",query="SELECT r FROM Residence where personId=:personId"),
})
public class Person implements Identifiable {

	private LongProperty id;

	private Salutation salutation;
	private StringProperty honorific;

	private StringProperty firstName;
	private StringProperty familyName;
	private StringProperty birthName;

	private ObjectProperty<LocalDate> birthday;
	private StringProperty birthplace;

	private Gender gender;
	private Residence preferredResidence;
	private List<Residence> residences;
	private Diocese diocese;
	private List<Role> roles;
	private List<Approval> approvals;
	private List<ContactMethod> contactMethods;
	private List<Participation> participations;
	private List<Relationship> relationships;
	private List<Responsible> responsibles;
	private List<YearAbroad> yearsAbroad;

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
	@Field
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
	@Field
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
	@Field
	public String getBirthName() {
		return birthName.get();
	}
	public void setBirthName(String birthName) {
		this.birthName.set(birthName);
	}
	public StringProperty birthNameProperty() {
		return birthName;
	}

	@Transient
	public String getFullName() { return getFirstName() + " " + getFamilyName(); }

	/**
	 * @return The birthday of a person
	 */
	@Column(nullable = true)
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
	@Field
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
	@Transient
	public Gender getGender(boolean nullSave) { return (nullSave && gender == null) ? new Gender() : gender; }

	/**
	 * A list of all places associated with this person.
	 * The list will be fetched lazy when it is needed.
	 */
	@OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE)
	public List<Residence> getResidences() {
		return residences;
	}
	public void setResidences(List<Residence> residences) {
		this.residences = residences;
	}

	/**
	 * The diocese that is responsable for this person.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="dioceseId")
	public Diocese getDiocese() {
		return diocese;
	}
	public void setDiocese(Diocese diocese) {
		this.diocese = diocese;
	}
	@Transient
	public Diocese getDiocese(boolean nullSave) { return (nullSave && diocese == null) ? new Diocese() : diocese; }

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
	 @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    public List<ContactMethod> getContactMethods() {
        return contactMethods;
    }
    public void setContactMethods(List<ContactMethod> contactMethods) {
        this.contactMethods = contactMethods;
    }

	/**
	 * A list of participations at events of this person.
	 * The list will be fetched lazy when it is needed.
	 */
	@OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE)
	public List<Participation> getParticipations() {
		return participations;
	}
	public void setParticipations(List<Participation> participations) {
		this.participations = participations;
	}

	/**
	 * A list of all relationships associated with this person.
	 * The list will be fetched lazy when it is needed.
	 */
	@OneToMany(mappedBy = "fromPerson", fetch = FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE)
	public List<Relationship> getRelationships() {
		return relationships;
	}
	public void setRelationships(List<Relationship> relationships) {
		this.relationships = relationships;
	}

	@OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
	public List<Responsible> getResponsibles() {
		return responsibles;
	}

	public void setResponsibles(List<Responsible> responsibles) {
		this.responsibles = responsibles;
	}

	@OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
	public List<YearAbroad> getYearsAbroad() {
		return yearsAbroad;
	}

	public void setYearsAbroad(List<YearAbroad> yearsAbroad) {
		this.yearsAbroad = yearsAbroad;
	}

	@OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
	public Residence getPreferredResidence() {
		return preferredResidence;
	}

	public void setPreferredResidence(Residence preferredResidence) {
		this.preferredResidence = preferredResidence;
	}

	@Transient
	@Override
	public String toString() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		return this.getFullName() + ((getBirthday() == null) ? "" : " ("+dtf.format(getBirthday())+")");
	}

	@Transient
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Person) && ((Person) obj).getId().equals(this.getId());
	}
}

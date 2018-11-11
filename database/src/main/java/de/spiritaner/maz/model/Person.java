package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.person.PersonEditorDialogController;
import de.spiritaner.maz.model.meta.*;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.view.binding.BindableProperty;
import javafx.beans.property.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Florian Böhm
 * @version 0.0.2
 */
@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = PersonEditorDialogController.class, identifiableName = "$person")
@Indexed
@NamedQueries({
		  @NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p"),
		  @NamedQuery(name = "Person.findAllIds", query = "SELECT p.id FROM Person p"),
		  @NamedQuery(name = "Person.findWithoutNewsletterApproval", query = "SELECT p.id FROM Person p JOIN p.approvals a JOIN a.approvalType at WHERE at.id=3 AND a.approved=FALSE"),
		  @NamedQuery(name = "Person.findWithRoleType", query = "SELECT p.id FROM Person p JOIN p.roles r JOIN r.roleType rt WHERE rt.id=:roleTypeId"),
})
public class Person extends PartialVolatileEntity implements Identifiable {

	public LongProperty id = new SimpleLongProperty();

	public StringProperty honorific = new SimpleStringProperty();
	public StringProperty firstName = new SimpleStringProperty();
	public StringProperty familyName = new SimpleStringProperty();
	public StringProperty birthName = new SimpleStringProperty();

	public ObjectProperty<LocalDate> birthday = new SimpleObjectProperty<>();
	public StringProperty birthplace = new SimpleStringProperty();

	public ObjectProperty<Salutation> salutation = new SimpleObjectProperty<>();
	public ObjectProperty<Gender> gender = new SimpleObjectProperty<>();
	public ObjectProperty<Diocese> diocese = new SimpleObjectProperty<>();
	public ObjectProperty<Residence> preferredResidence = new SimpleObjectProperty<>();
	public ObjectProperty<Religion> religion = new SimpleObjectProperty<>();

	private List<Residence> residences = new ArrayList<>();
	private List<Role> roles = new ArrayList<>();
	private List<Approval> approvals = new ArrayList<>();
	private List<ContactMethod> contactMethods = new ArrayList<>();
	private List<Participation> participations = new ArrayList<>();
	private List<Relationship> relationships = new ArrayList<>();
	private List<Responsible> responsibles = new ArrayList<>();
	private List<YearAbroad> yearsAbroad = new ArrayList<>();
	private List<ExperienceAbroad> experiencesAbroad = new ArrayList<>();

	/**
	 * Every person has a composite editable to track changes made to the dataset.
	 *
	 * @return The composite editable of a person
	 */
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

	/**
	 * The salutation of a person is used for the german T-V distinction.
	 * This specific aspect reflects the level of politeness.
	 *
	 * @return The salutation of a person
	 */
	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
	@JoinColumn(name = "salutationId")
	public Salutation getSalutation() {
		return salutation.get();
	}

	public void setSalutation(Salutation salutation) {
		this.salutation.set(salutation);
	}

	@BindableProperty
	public ObjectProperty<Salutation> salutationProperty() {
		return salutation;
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

	@BindableProperty
	public StringProperty honorificProperty() {
		return honorific;
	}

	/**
	 * @return The persons first editable.
	 */
	@Column(nullable = false)
	@Field
	public String getFirstName() {
		return firstName.get();
	}

	public void setFirstName(String firstName) {
		this.firstName.set(firstName);
	}

	@BindableProperty
	public StringProperty firstNameProperty() {
		return firstName;
	}

	/**
	 * The persons family editable. When this person marries its family editable may be overridden.
	 * The original family editable will then become the birth editable.
	 *
	 * @return The persons family editable
	 */
	@Column(nullable = false)
	@Field
	public String getFamilyName() {
		return familyName.get();
	}

	public void setFamilyName(String familyName) {
		this.familyName.set(familyName);
	}

	@BindableProperty
	public StringProperty familyNameProperty() {
		return familyName;
	}

	/**
	 * The persons birth editable. It is empty until the person marries another person.
	 *
	 * @return The persons birth editable, may be empty
	 */
	@Field
	public String getBirthName() {
		return birthName.get();
	}

	public void setBirthName(String birthName) {
		this.birthName.set(birthName);
	}

	@BindableProperty
	public StringProperty birthNameProperty() {
		return birthName;
	}

	@Transient
	public String getFullName() {
		return getFirstName() + " " + getFamilyName();
	}

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

	@BindableProperty
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

	@BindableProperty
	public StringProperty birthplaceProperty() {
		return birthplace;
	}

	/**
	 * One person can only have one gender at a time. If the person has a specific (maybe mixed) gender,
	 * the gender can be created first and then be assigned to the person.
	 *
	 * @return The gender of the person
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "genderId")
	//@NotFound(action = NotFoundAction.IGNORE)
	public Gender getGender() {
		return gender.get();
	}

	public void setGender(Gender gender) {
		this.gender.set(gender);
	}

	@BindableProperty
	public ObjectProperty<Gender> genderProperty() {
		return gender;
	}

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
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "dioceseId")
	public Diocese getDiocese() {
		return diocese.get();
	}

	public void setDiocese(Diocese diocese) {
		this.diocese.set(diocese);
	}

	@BindableProperty
	public ObjectProperty<Diocese> dioceseProperty() {
		return diocese;
	}

	/**
	 * All the roles this specific person has.
	 */
	@OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	/**
	 * All the approvals this specific person has.
	 */
	@OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE)
	public List<Approval> getApprovals() {
		return approvals;
	}

	public void setApprovals(List<Approval> approvals) {
		this.approvals = approvals;
	}

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
	public List<ExperienceAbroad> getExperiencesAbroad() {
		return experiencesAbroad;
	}

	public void setExperiencesAbroad(List<ExperienceAbroad> experiencesAbroad) {
		this.experiencesAbroad = experiencesAbroad;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "preferredResidenceId")
	public Residence getPreferredResidence() {
		return preferredResidence.get();
	}

	public void setPreferredResidence(Residence preferredResidence) {
		this.preferredResidence.set(preferredResidence);
	}

	public ObjectProperty<Residence> preferredResidenceProperty() {
		return preferredResidence;
	}

	/**
	 * The religion or confession that this person has choosen.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "religionId")
	public Religion getReligion() {
		return religion.get();
	}

	public void setReligion(Religion religion) {
		this.religion.set(religion);
	}

	@BindableProperty
	public ObjectProperty<Religion> religionProperty() {
		return religion;
	}

	@Transient
	@Override
	protected void initialize() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		TypedQuery<YearAbroad> query = em.createNamedQuery("YearAbroad.findCurrentOfPerson", YearAbroad.class);
		query.setParameter("person", this);
		query.setParameter("today", LocalDate.now());

		for (YearAbroad yearAbroad : query.getResultList()) {
			if(yearAbroad.getAbortionDate() == null ||
					  LocalDate.now().isBefore(yearAbroad.getAbortionDate()) ||
					  LocalDate.now().isEqual(yearAbroad.getAbortionDate())) {
				Residence tmpRes = new Residence();
				tmpRes.setId(-1L);
				tmpRes.setPerson(this);
				tmpRes.setAddress(yearAbroad.getSite().getAddress());
				tmpRes.setResidenceType(em.find(ResidenceType.class, 3L));
				tmpRes.setForPost(true);
				this.setPreferredResidence(tmpRes);
				// TODO take care of multiple residences with same id -1
				if (!residences.contains(tmpRes)) residences.add(tmpRes);
			}
		}
	}

	@Transient
	@Override
	public String toString() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		return this.getFullName() + ((getBirthday() == null) ? "" : " (" + dtf.format(getBirthday()) + ")");
	}

	@Transient
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Person) && ((Person) obj).getId().equals(this.getId());
	}
}

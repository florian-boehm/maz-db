package de.spiritaner.maz.model;

import javafx.beans.property.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p"),
	//@NamedQuery(name="Person.fetchAllResidences",query="SELECT r FROM Residence where personId=:personId"),
})
public class Person {

	private LongProperty id;
	private StringProperty prename;
	private StringProperty surname;
	private ObjectProperty<LocalDate> birthday;
	private StringProperty birthplace;
	private Gender gender;
	private List<Residence> residences;
	private Address diocese;

	public Person() {
		id = new SimpleLongProperty();
		prename = new SimpleStringProperty();
		surname = new SimpleStringProperty();
		birthday = new SimpleObjectProperty<>();
		birthplace = new SimpleStringProperty();
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id.get();
	}

	public void setId(Long id) {
		this.id.set(id);
	}

	public LongProperty id() {
		return id;
	}

	@Column(nullable = false)
	public String getPrename() {
		return prename.get();
	}

	public void setPrename(String prename) {
		this.prename.set(prename);
	}

	public StringProperty prenameProperty() {
		return prename;
	}

	@Column(nullable = false)
	public String getSurname() {
		return surname.get();
	}

	public void setSurname(String surname) {
		this.surname.set(surname);
	}

	public StringProperty surnameProperty() {
		return surname;
	}

	@Column(nullable = false)
	//@Temporal(TemporalType.TIMESTAMP)
	public LocalDate getBirthday() {
		return birthday.get();
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday.set(birthday);
	}

	public ObjectProperty<LocalDate> birthday() {
		return birthday;
	}

	public String getBirthplace() {
		return birthplace.get();
	}

	public void setBirthplace(String birthplace) {
		this.birthplace.set(birthplace);
	}

	public StringProperty birthplace() {
		return birthplace;
	}

	@ManyToOne(fetch=FetchType.EAGER, cascade = {CascadeType.PERSIST})
	@JoinColumn(name="genderId")
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	//	@Transient
	@OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
	public List<Residence> getResidences() {
		return residences;
	}

	public void setResidences(List<Residence> residences) {
		this.residences = residences;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="dioceseAddress")
	public Address getDiocese() {
		return diocese;
	}

	public void setDiocese(Address diocese) {
		this.diocese = diocese;
	}
}

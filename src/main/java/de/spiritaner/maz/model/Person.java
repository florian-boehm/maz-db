package de.spiritaner.maz.model;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
public class Person {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String prename;

	@Column(nullable = false)
	private String surname;

	@Column(nullable = false)
	private Date birthday;

	private String birthplace;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="genderId")
	@Column(nullable = false)
	private Gender gender;

	private String diocese;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPrename() {
		return prename;
	}

	public void setPrename(String prename) {
		this.prename = prename;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getBirthplace() {
		return birthplace;
	}

	public void setBirthplace(String birthplace) {
		this.birthplace = birthplace;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getDiocese() {
		return diocese;
	}

	public void setDiocese(String diocese) {
		this.diocese = diocese;
	}
}

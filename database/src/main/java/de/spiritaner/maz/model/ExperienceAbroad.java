package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.experienceabroad.ExperienceAbroadEditorDialogController;
import javafx.beans.property.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * The experience abroad, in German 'Mitlebezeit', is a smaller
 *
 * @author Florian Schwab
 */
@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = ExperienceAbroadEditorDialogController.class, identifiableName = "Mitlebezeit")
public class ExperienceAbroad implements Identifiable {

	public LongProperty id = new SimpleLongProperty();
	public ObjectProperty<Person> person = new SimpleObjectProperty<>();
	public StringProperty community = new SimpleStringProperty();
	public StringProperty details = new SimpleStringProperty();
	public StringProperty location = new SimpleStringProperty();
	public ObjectProperty<LocalDate> fromDate = new SimpleObjectProperty<>();
	public ObjectProperty<LocalDate> toDate = new SimpleObjectProperty<>();

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

	/**
	 * The person who has done this experience abroad.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="personId", nullable = false)
	public Person getPerson() {
		return person.get();
	}
	public void setPerson(Person person) {
		this.person.set(person);
	}
	public ObjectProperty<Person> personProperty() {
		return person;
	}

	@Column(nullable = false)
	public String getCommunity() {
		return community.get();
	}
	public StringProperty communityProperty() {
		return community;
	}
	public void setCommunity(String community) {
		this.community.set(community);
	}

	public String getDetails() {
		return details.get();
	}
	public StringProperty detailsProperty() {
		return details;
	}
	public void setDetails(String details) {
		this.details.set(details);
	}

	@Column(nullable = false)
	public String getLocation() {
		return location.get();
	}
	public StringProperty locationProperty() {
		return location;
	}
	public void setLocation(String location) {
		this.location.set(location);
	}

	@Column(nullable = false)
	public LocalDate getFromDate() {
		return fromDate.get();
	}
	public ObjectProperty<LocalDate> fromDateProperty() {
		return fromDate;
	}
	public void setFromDate(LocalDate fromDate) {
		this.fromDate.set(fromDate);
	}

	@Column(nullable = false)
	public LocalDate getToDate() {
		return toDate.get();
	}
	public ObjectProperty<LocalDate> toDateProperty() {
		return toDate;
	}
	public void setToDate(LocalDate toDate) {
		this.toDate.set(toDate);
	}
}

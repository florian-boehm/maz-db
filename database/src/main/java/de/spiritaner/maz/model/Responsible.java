package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.yearabroad.ResponsibleEditorDialogController;
import de.spiritaner.maz.model.meta.PersonGroup;
import javafx.beans.property.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = ResponsibleEditorDialogController.class, identifiableName = "$responsible")
@NamedQueries({
		  @NamedQuery(name = "Responsible.findJobDescriptionsDistinct", query = "SELECT DISTINCT(r.jobDescription) FROM Responsible r"),
		  @NamedQuery(name = "Responsible.findJobDescriptionsDistinctForSite", query = "SELECT DISTINCT(r.jobDescription) FROM Responsible r WHERE r.site=:site"),
		  @NamedQuery(name = "Responsible.findHomeCountriesDistinct", query = "SELECT DISTINCT(r.homeCountry) FROM Responsible r"),
		  @NamedQuery(name = "Responsible.findHomeCountriesDistinctForSite", query = "SELECT DISTINCT(r.homeCountry) FROM Responsible r WHERE r.site=:site")
})
public class Responsible implements Identifiable {

	public LongProperty id = new SimpleLongProperty();
	public ObjectProperty<Person> person = new SimpleObjectProperty<>();
	public ObjectProperty<Site> site = new SimpleObjectProperty<>();
	public StringProperty homeCountry = new SimpleStringProperty();
	public StringProperty jobDescription = new SimpleStringProperty();
	public ObjectProperty<PersonGroup> personGroup = new SimpleObjectProperty<>();

	@Override
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

	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "siteId", nullable = false)
	public Site getSite() {
		return site.get();
	}
	public void setSite(Site site) {
		this.site.set(site);
	}
	public ObjectProperty<Site> siteProperty() {
		return site;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "personGroupId", nullable = false)
	public PersonGroup getPersonGroup() {
		return personGroup.get();
	}
	public void setPersonGroup(PersonGroup personGroup) {
		this.personGroup.set(personGroup);
	}
	public ObjectProperty<PersonGroup> personGroupProperty() {
		return personGroup;
	}

	public String getHomeCountry() {
		return homeCountry.get();
	}
	public StringProperty homeCountryProperty() {
		return homeCountry;
	}
	public void setHomeCountry(String homeCountry) {
		this.homeCountry.set(homeCountry);
	}

	public String getJobDescription() {
		return jobDescription.get();
	}
	public StringProperty jobDescriptionProperty() {
		return jobDescription;
	}
	public void setJobDescription(String jobDescription) {
		this.jobDescription.set(jobDescription);
	}

	@Transient
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Responsible) && ((Responsible) obj).getId().equals(this.getId());
	}
}

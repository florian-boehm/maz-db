package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.yearabroad.ResponsibleEditorDialogController;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = ResponsibleEditorDialogController.class, identifiableName = "Verantwortliche(n)")
@NamedQueries({
		  @NamedQuery(name = "Responsible.findGroupNamesDistinct", query = "SELECT DISTINCT(r.groupName) FROM Responsible r"),
		  @NamedQuery(name = "Responsible.findGroupNamesDistinctForSite", query = "SELECT DISTINCT(r.groupName) FROM Responsible r WHERE r.site=:site"),
		  @NamedQuery(name = "Responsible.findJobDescriptionsDistinct", query = "SELECT DISTINCT(r.jobDescription) FROM Responsible r"),
		  @NamedQuery(name = "Responsible.findJobDescriptionsDistinctForSite", query = "SELECT DISTINCT(r.jobDescription) FROM Responsible r WHERE r.site=:site"),
		  @NamedQuery(name = "Responsible.findHomeCountriesDistinct", query = "SELECT DISTINCT(r.homeCountry) FROM Responsible r"),
		  @NamedQuery(name = "Responsible.findHomeCountriesDistinctForSite", query = "SELECT DISTINCT(r.homeCountry) FROM Responsible r WHERE r.site=:site")
})
public class Responsible implements Identifiable {

	private LongProperty id;
	private Person person;
	private Site site;
	private StringProperty homeCountry;
	private StringProperty jobDescription;
	private StringProperty groupName;

	public Responsible() {
		id = new SimpleLongProperty();
		groupName = new SimpleStringProperty();
		homeCountry = new SimpleStringProperty();
		jobDescription = new SimpleStringProperty();
	}

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
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "siteId", nullable = false)
	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public String getGroupName() {
		return groupName.get();
	}

	public StringProperty groupNameProperty() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName.set(groupName);
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

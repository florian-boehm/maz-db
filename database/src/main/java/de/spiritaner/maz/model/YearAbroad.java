package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.yearabroad.YearAbroadEditorDialogController;
import javafx.beans.property.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * @author Florian Böhm
 */
@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = YearAbroadEditorDialogController.class, identifiableName = "$year_abroad")
@NamedQueries({
		  @NamedQuery(name = "YearAbroad.findAllOfSiteWithinDate", query = "SELECT ya FROM YearAbroad ya WHERE ya.site=:site AND ((ya.departureDate BETWEEN :departureDate AND :arrivalDate) OR (ya.arrivalDate BETWEEN :departureDate AND :arrivalDate))"),
		  @NamedQuery(name = "YearAbroad.findAllOfSiteInYear", query = "SELECT ya FROM YearAbroad ya WHERE ya.site=:site AND YEAR(ya.departureDate) = :year"),
		  @NamedQuery(name = "YearAbroad.findCurrentOfPerson", query = "SELECT ya FROM YearAbroad ya WHERE ya.person=:person AND (:today BETWEEN ya.departureDate AND ya.arrivalDate)")
})
public class YearAbroad implements Identifiable {

	public LongProperty id = new SimpleLongProperty();
	public ObjectProperty<Person> person = new SimpleObjectProperty<>();
	public ObjectProperty<Site> site = new SimpleObjectProperty<>();

	public ObjectProperty<LocalDate> departureDate = new SimpleObjectProperty<>();
	public ObjectProperty<LocalDate> arrivalDate = new SimpleObjectProperty<>();
	public ObjectProperty<LocalDate> abortionDate = new SimpleObjectProperty<>();
	public ObjectProperty<LocalDate> missionDate = new SimpleObjectProperty<>();

	public StringProperty jobDescription = new SimpleStringProperty();
	public StringProperty details = new SimpleStringProperty();
	public StringProperty abortionReason = new SimpleStringProperty();

	public BooleanProperty wwPromoted = new SimpleBooleanProperty();
	public IntegerProperty wwMonths = new SimpleIntegerProperty();
	public ObjectProperty<EPNumber> epNumber = new SimpleObjectProperty<>();

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
     * The person who has done this year abroad.
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

	/**
     * The site (Einsatzstelle) to which this year abroad is connected to.
     */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="siteId", nullable = false)
	public Site getSite() {
		return site.get();
	}
	public void setSite(Site site) {
		this.site.set(site);
	}

	public ObjectProperty<Site> siteProperty() {
		return site;
	}

	@Column(nullable = false)
	public LocalDate getDepartureDate() {
		return departureDate.get();
	}
	public ObjectProperty<LocalDate> departureDateProperty() {
		return departureDate;
	}
	public void setDepartureDate(LocalDate departureDate) {
		this.departureDate.set(departureDate);
	}

	@Column(nullable = false)
	public LocalDate getArrivalDate() {
		return arrivalDate.get();
	}
	public ObjectProperty<LocalDate> arrivalDateProperty() {
		return arrivalDate;
	}
	public void setArrivalDate(LocalDate arrivalDate) {
		this.arrivalDate.set(arrivalDate);
	}

	public LocalDate getAbortionDate() {
		return abortionDate.get();
	}
	public ObjectProperty<LocalDate> abortionDateProperty() {
		return abortionDate;
	}
	public void setAbortionDate(LocalDate abortionDate) {
		this.abortionDate.set(abortionDate);
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

	public String getDetails() {
		return details.get();
	}
	public StringProperty detailsProperty() {
		return details;
	}
	public void setDetails(String details) {
		this.details.set(details);
	}

	public String getAbortionReason() {
		return abortionReason.get();
	}
	public StringProperty abortionReasonProperty() {
		return abortionReason;
	}
	public void setAbortionReason(String abortionReason) {
		this.abortionReason.set(abortionReason);
	}

	public boolean getWwPromoted() {
		return wwPromoted.get();
	}
	public BooleanProperty wwPromotedProperty() {
		return wwPromoted;
	}
	public void setWwPromoted(boolean wwPromoted) {
		this.wwPromoted.set(wwPromoted);
	}

	public LocalDate getMissionDate() {
		return missionDate.get();
	}
	public ObjectProperty<LocalDate> missionDateProperty() {
		return missionDate;
	}
	public void setMissionDate(LocalDate missionDate) {
		this.missionDate.set(missionDate);
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="epNumberId", nullable = false)
	public EPNumber getEpNumber() {
		return epNumber.get();
	}
	public ObjectProperty<EPNumber> epNumberProperty() {
		return epNumber;
	}
	public void setEpNumber(EPNumber epNumber) {
		this.epNumber.set(epNumber);
	}

	public int getWwMonths() {
		return wwMonths.get();
	}
	public IntegerProperty wwMonthsProperty() {
		return wwMonths;
	}
	public void setWwMonths(int wwMonths) {
		this.wwMonths.set(wwMonths);
	}

	@Transient
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof YearAbroad) && ((YearAbroad) obj).getId().equals(this.getId());
	}
}

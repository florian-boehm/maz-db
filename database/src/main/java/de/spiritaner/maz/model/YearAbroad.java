package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.yearabroad.YearAbroadEditorDialogController;
import javafx.beans.property.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * @author Florian Schwab
 */
@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = YearAbroadEditorDialogController.class, identifiableName = "Auslandsjahr")
@NamedQueries({
		  @NamedQuery(name = "YearAbroad.findAllOfSiteWithinDate", query = "SELECT ya FROM YearAbroad ya WHERE ya.site=:site AND ((ya.departureDate BETWEEN :departureDate AND :arrivalDate) OR (ya.arrivalDate BETWEEN :departureDate AND :arrivalDate))"),
		  @NamedQuery(name = "YearAbroad.findCurrentOfPerson", query = "SELECT ya FROM YearAbroad ya WHERE ya.person=:person AND (:today BETWEEN ya.departureDate AND ya.arrivalDate)")
})
public class YearAbroad implements Identifiable {

	private LongProperty id;
	private ObjectProperty<Person> person;
	private ObjectProperty<Site> site;

	private ObjectProperty<LocalDate> departureDate;
	private ObjectProperty<LocalDate> arrivalDate;
	private ObjectProperty<LocalDate> abortionDate;
	private ObjectProperty<LocalDate> missionDate;

	private StringProperty jobDescription;
	private StringProperty details;
	private StringProperty abortionReason;

	private BooleanProperty weltwaertsPromoted;
	private ObjectProperty<EPNumber> epNumber;

	public YearAbroad() {
		id = new SimpleLongProperty();
		departureDate = new SimpleObjectProperty<>();
		arrivalDate = new SimpleObjectProperty<>();
		jobDescription = new SimpleStringProperty();
		details = new SimpleStringProperty();
		weltwaertsPromoted = new SimpleBooleanProperty();
		abortionDate = new SimpleObjectProperty<>();
		abortionReason = new SimpleStringProperty();
		missionDate = new SimpleObjectProperty<>();
		person = new SimpleObjectProperty<>();
		site = new SimpleObjectProperty<>();
		epNumber = new SimpleObjectProperty<>();
	}

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

	public boolean isWeltwaertsPromoted() {
		return weltwaertsPromoted.get();
	}

	public BooleanProperty weltwaertsPromotedProperty() {
		return weltwaertsPromoted;
	}

	public void setWeltwaertsPromoted(boolean weltwaertsPromoted) {
		this.weltwaertsPromoted.set(weltwaertsPromoted);
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

	@Transient
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof YearAbroad) && ((YearAbroad) obj).getId().equals(this.getId());
	}

	public ObjectProperty<EPNumber> epNumberProperty() {
		return epNumber;
	}

	public void setEpNumber(EPNumber epNumber) {
		this.epNumber.set(epNumber);
	}
}

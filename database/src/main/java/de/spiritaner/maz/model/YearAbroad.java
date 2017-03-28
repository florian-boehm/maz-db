package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.role.RoleEditorDialogController;
import de.spiritaner.maz.controller.yearabroad.YearAbroadEditorDialogController;
import javafx.beans.property.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = YearAbroadEditorDialogController.class, identifiableName = "Auslandsjahr")
public class YearAbroad implements Identifiable {

	private LongProperty id;
	private Person person;
	private Site site;

	private ObjectProperty<LocalDate> departureDate;
	private ObjectProperty<LocalDate> arrivalDate;
	private ObjectProperty<LocalDate> abortionDate;

	private StringProperty jobDescription;
	private StringProperty details;
	private StringProperty abortionReason;

	private BooleanProperty weltwaertsPromoted;

	public YearAbroad() {
		id = new SimpleLongProperty();
		departureDate = new SimpleObjectProperty<>();
		arrivalDate = new SimpleObjectProperty<>();
		jobDescription = new SimpleStringProperty();
		details = new SimpleStringProperty();
		weltwaertsPromoted = new SimpleBooleanProperty();
		abortionDate = new SimpleObjectProperty<>();
		abortionReason = new SimpleStringProperty();
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
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}

    /**
     * The site (Einsatzstelle) to which this year abroad is connected to.
     */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="siteId", nullable = false)
	public Site getSite() {
		return site;
	}
	public void setSite(Site site) {
		this.site = site;
	}

	public LocalDate getDepartureDate() {
		return departureDate.get();
	}

	public ObjectProperty<LocalDate> departureDateProperty() {
		return departureDate;
	}

	public void setDepartureDate(LocalDate departureDate) {
		this.departureDate.set(departureDate);
	}

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

	@Transient
	public StringProperty weltwaertsPromotedStringProperty() {
		return new SimpleStringProperty((weltwaertsPromoted.get()) ? "Ja" : "Nein");
	}

	public void setWeltwaertsPromoted(boolean weltwaertsPromoted) {
		this.weltwaertsPromoted.set(weltwaertsPromoted);
	}
}

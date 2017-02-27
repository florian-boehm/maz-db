package de.spiritaner.maz.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.Year;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@Audited
public class YearAbroad {

	private LongProperty id;
	private Person person;
	private Site site;

	public YearAbroad() {
		id = new SimpleLongProperty();
	}

	@Id
	@GeneratedValue
	public long getId() {
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
}

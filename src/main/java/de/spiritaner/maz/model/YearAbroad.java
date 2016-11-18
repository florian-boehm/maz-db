package de.spiritaner.maz.model;

import javafx.beans.property.LongProperty;

import javax.persistence.*;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
public class YearAbroad {

	@Id
	@GeneratedValue
	private LongProperty id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="personId", nullable = false)
	private Person person;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="siteId", nullable = false)
	private Site site;


}

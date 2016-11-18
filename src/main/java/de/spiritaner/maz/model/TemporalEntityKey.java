package de.spiritaner.maz.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * The TemporalEntityKey represents a composite key of a long property and a date property.
 * The long property is a surrogate key and does not change when the dataset is changed.
 * The date property is a natural key and does change whenever the dataset is changed.
 *
 * @author Florian Schwab
 * @version 0.0.2
 */
@Embeddable
public class TemporalEntityKey implements Serializable {

	private LongProperty id;
	private ObjectProperty<LocalDate> validFromDate;

	/**
	 * @return The surrogate key
	 */
	@GeneratedValue
	@Column(nullable = false, updatable = false)
	public Long getId() {
		return id.get();
	}
	public void setId(Long id) {
		this.id.set(id);
	}
	public LongProperty idProperty() {
		return id;
	}

	@Column(nullable = false, updatable = false)
	public LocalDate getValidFromDate() {
		return validFromDate.get();
	}
	public void setValidFromDate(LocalDate validFromDate) {
		this.validFromDate.set(validFromDate);
	}
	public ObjectProperty<LocalDate> validFromDateProperty() { return validFromDate; }

	@PrePersist
	public void fillDates() {
		this.validFromDate.set(LocalDate.now(ZoneId.of("Europe/Berlin")));
	}

	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj == this) {
			return true;
		} else if(obj != null && obj instanceof TemporalEntityKey) {
			TemporalEntityKey pk = (TemporalEntityKey) obj;
			return pk.getId().equals(getId()) && pk.getValidFromDate().equals(getValidFromDate());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, validFromDate);
	}
}

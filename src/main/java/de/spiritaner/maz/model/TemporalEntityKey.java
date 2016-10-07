package de.spiritaner.maz.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.PrePersist;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Deprecated
public class TemporalEntityKey implements Serializable {
	@Basic
	private Long id;

	@Column(nullable = false, updatable = false)
	private Date validFromDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getValidFromDate() {
		return validFromDate;
	}

	public void setValidFromDate(Date validFromDate) {
		this.validFromDate = validFromDate;
	}

	@PrePersist
	public void fillDates() {
		this.validFromDate = Calendar.getInstance(Locale.GERMANY).getTime();
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

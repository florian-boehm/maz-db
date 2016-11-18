package de.spiritaner.maz.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;

/**
 * The salutation class is used for the T-V distinction. The level of politeness is necessary for the german language.
 *
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "Salutation.findAll", query = "SELECT s FROM Salutation s"),
})
public class Salutation {

	private LongProperty id = new SimpleLongProperty();
	private StringProperty description = new SimpleStringProperty();

	@Id
	@GeneratedValue
	public Long getId() {
		return id.get();
	}
	public void setId(Long id) {
		this.id.set(id);
	}
	public LongProperty id() {
		return id;
	}

	@Column(nullable = false)
	public String getDescription() {
		return description.get();
	}
	public void setDescription(String description) {
		this.description.set(description);
	}
	public StringProperty description() {
		return description;
	}

	@Override
	public String toString() {
		return description.get();
	}

	@Override
	public int hashCode() {
		return id.hashCode()+description.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof Salutation) {
			return ((Salutation) object).id.get() == id.get();
		} else {
			return false;
		}
	}
}

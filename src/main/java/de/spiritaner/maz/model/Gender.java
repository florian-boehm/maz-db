package de.spiritaner.maz.model;

import javafx.beans.property.*;

import javax.persistence.*;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "Gender.findAll", query = "SELECT g FROM Gender g"),
})
public class Gender {

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
		if(object instanceof Gender) {
			return ((Gender) object).id.get() == id.get();
		} else {
			return false;
		}
	}
}

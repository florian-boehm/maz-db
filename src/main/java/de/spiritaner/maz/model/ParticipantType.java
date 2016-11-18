package de.spiritaner.maz.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
public class ParticipantType {

	private LongProperty id;
	private StringProperty description;

	public ParticipantType() {
		id = new SimpleLongProperty();
		description = new SimpleStringProperty();
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id.get();
	}
	public void setId(Long id) {
		this.id.set(id);
	}
	public LongProperty idProperty() { return id; }

	@Column(nullable = false)
	public String getDescription() {
		return description.get();
	}
	public void setDescription(String description) {
		this.description.set(description);
	}
	public StringProperty descriptionProperty() { return description; }
}

package de.spiritaner.maz.model.meta;

import de.spiritaner.maz.model.Identifiable;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;
import java.util.List;

@MappedSuperclass
abstract public class MetaClass implements Identifiable {

	private LongProperty id = new SimpleLongProperty();
	private StringProperty description = new SimpleStringProperty();

	@Id
	@GeneratedValue/*(strategy = GenerationType.TABLE)*/
	public Long getId() {
		return id.get();
	}

	public void setId(Long id) {
		this.id.set(id);
	}

	public LongProperty idProperty() {
		return id;
	}

	@Column(nullable = false)
	public String getDescription() {
		return description.get();
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	@Override
	public String toString() {
		return (id.get() == 0L) ? "" : id.get() + " - " + description.get();
	}

	@Override
	public int hashCode() {
		return id.hashCode() + description.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return (object instanceof MetaClass) && ((MetaClass) object).getId().equals(this.getId());
	}
}

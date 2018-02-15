package de.spiritaner.maz.model.meta;

import de.spiritaner.maz.model.Identifiable;
import javafx.beans.property.*;

import javax.persistence.*;

@MappedSuperclass
abstract public class MetaClass implements Identifiable {

	private LongProperty id = new SimpleLongProperty();
	private StringProperty description = new SimpleStringProperty();
	private BooleanProperty removable = new SimpleBooleanProperty(Boolean.TRUE);
	private BooleanProperty editable = new SimpleBooleanProperty(Boolean.TRUE);

	@Id
	@TableGenerator(name="tab", initialValue = 100, allocationSize = 5)
	@GeneratedValue(strategy=GenerationType.TABLE, generator="tab")
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

	@Column(nullable = false)
	public boolean isRemovable() {
		return removable.get();
	}

	public BooleanProperty removableProperty() {
		return removable;
	}

	public void setRemovable(boolean removable) {
		this.removable.set(removable);
	}

	@Column(nullable = false)
	public boolean isEditable() {
		return editable.get();
	}

	public BooleanProperty editableProperty() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable.set(editable);
	}

	@Transient
	@Override
	public String toString() {
		return (id.get() == 0L) ? "" : /*id.get() + " - " +*/ description.get();
	}

	@Transient
	@Override
	public int hashCode() {
		return id.hashCode() + description.hashCode();
	}

	@Transient
	@Override
	public boolean equals(Object object) {
		return (object instanceof MetaClass) && ((MetaClass) object).getId().equals(this.getId());
	}
}

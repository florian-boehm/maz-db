package de.spiritaner.maz.model.meta;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class MetaClass {

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
        return description.get();
    }
    @Override
    public int hashCode() {
        return id.hashCode()+description.hashCode();
    }
    @Override
    public boolean equals(Object object) {
        if(object instanceof MetaClass) {
            return ((MetaClass) object).id.get() == id.get();
        } else {
            return false;
        }
    }
}

package de.spiritaner.maz.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;
import java.util.List;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
public class RoleType {

	private LongProperty id;
	private StringProperty description;
	private List<Role> roles;

	public RoleType() {
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

	/**
	 * The description of the role type.
	 */
	@Column(nullable = false)
	public String getDescription() {
		return description.get();
	}
	public void setDescription(String description) {
		this.description.set(description);
	}
	public StringProperty descriptionProperty() { return description; }

	/**
	 * All roles that use this role type.
	 */
	@OneToMany(mappedBy = "roleType", fetch = FetchType.LAZY)
	public List<Role> getRoles() { return roles; }
	public void setRoles(List<Role> roles) { this.roles = roles; }
}

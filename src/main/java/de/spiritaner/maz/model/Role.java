package de.spiritaner.maz.model;

import de.spiritaner.maz.model.meta.RoleType;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@Audited
public class Role {

	private LongProperty id;
	private Person person;
	private RoleType roleType;

	public Role() {
		id = new SimpleLongProperty();
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
	 * The person that owns this role.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="personId", nullable = false)
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * The role type that this role is using.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "roleTypeId", nullable = false)
	public RoleType getRoleType() {
		return roleType;
	}
	public void setRoleType(RoleType roleType) {
		this.roleType = roleType;
	}
}

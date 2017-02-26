package de.spiritaner.maz.model.meta;

import de.spiritaner.maz.model.Role;

import javax.persistence.*;
import java.util.List;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
public class RoleType extends MetaClass {

	private List<Role> roles;

	/**
	 * All roles that use this role type.
	 */
	@OneToMany(mappedBy = "roleType", fetch = FetchType.LAZY)
	public List<Role> getRoles() { return roles; }
	public void setRoles(List<Role> roles) { this.roles = roles; }
}

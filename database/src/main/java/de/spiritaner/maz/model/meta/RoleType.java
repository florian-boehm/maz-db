package de.spiritaner.maz.model.meta;

import de.spiritaner.maz.model.Role;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * @author Florian Böhm
 * @version 0.0.1
 */
@Entity
@Audited(targetAuditMode = NOT_AUDITED)
@NamedQueries({
        @NamedQuery(name = "RoleType.findAll", query = "SELECT rt FROM RoleType rt"),
        @NamedQuery(name = "RoleType.findByDesc", query = "SELECT rt FROM RoleType rt WHERE rt.description=:description"),
})
public class RoleType extends MetaClass {

    private List<Role> roles;

    /**
     * All roles that use this role type.
     */
    @OneToMany(mappedBy = "roleType", fetch = FetchType.LAZY)
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

}

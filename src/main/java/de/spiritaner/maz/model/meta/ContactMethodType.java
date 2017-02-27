package de.spiritaner.maz.model.meta;


import de.spiritaner.maz.model.ContactMethod;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@Audited(targetAuditMode = NOT_AUDITED)
@NamedQueries({
        @NamedQuery(name = "ContactMethodType.findAll", query = "SELECT cmt FROM ContactMethodType cmt"),
})
public class ContactMethodType extends MetaClass {

    private List<ContactMethod> contactMethods;

    /**
     * All contact methods that use this contact method type.
     */
    @OneToMany(mappedBy = "contactMethodType", fetch = FetchType.LAZY)
    public List<ContactMethod> getContactMethods() {
        return contactMethods;
    }

    public void setContactMethods(List<ContactMethod> contactMethods) {
        this.contactMethods = contactMethods;
    }
}

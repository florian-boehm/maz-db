package de.spiritaner.maz.model.meta;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * @author Florian Schwab
 */
@Entity
@Audited(targetAuditMode = NOT_AUDITED)
@NamedQueries({
        @NamedQuery(name = "PersonGroup.findAll", query = "SELECT pg FROM PersonGroup pg"),
        @NamedQuery(name = "PersonGroup.findByDesc", query = "SELECT pg FROM PersonGroup pg WHERE pg.description=:description"),
})
public class PersonGroup extends MetaClass {

}

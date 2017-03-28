package de.spiritaner.maz.model.meta;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@Audited(targetAuditMode = NOT_AUDITED)
@NamedQueries({
        @NamedQuery(name = "EventType.findAll", query = "SELECT et FROM EventType et"),
})
public class EventType extends MetaClass {

}

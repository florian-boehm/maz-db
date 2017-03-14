package de.spiritaner.maz.model.meta;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * Created by Florian on 3/14/2017.
 */
@Entity
@Audited(targetAuditMode = NOT_AUDITED)
@NamedQueries({
		  @NamedQuery(name = "RelationshipType.findAll", query = "SELECT rt FROM RelationshipType rt"),
})
public class RelationshipType extends MetaClass {
}

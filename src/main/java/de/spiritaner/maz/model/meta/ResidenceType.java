package de.spiritaner.maz.model.meta;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@Audited(targetAuditMode = NOT_AUDITED)
public class ResidenceType extends MetaClass {

}

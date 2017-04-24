package de.spiritaner.maz.model.meta;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * The salutation class is used for the T-V distinction. The level of politeness is necessary for the german language.
 *
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@Audited(targetAuditMode = NOT_AUDITED)
@NamedQueries({
		  @NamedQuery(name = "Salutation.findAll", query = "SELECT s FROM Salutation s"),
})
public class Salutation extends MetaClass {

	public static Salutation createEmpty() {
		Salutation result = new Salutation();
		result.setDescription("-/-");
		result.setId(-1L);
		return result;
	}
}

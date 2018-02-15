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
        @NamedQuery(name = "Gender.findAll", query = "SELECT g FROM Gender g ORDER BY g.description"),
})
public class Gender extends MetaClass {

	public static Gender createEmpty() {
		Gender result = new Gender();
		result.setDescription("-/-");
		result.setId(-1L);
		return result;
	}
}

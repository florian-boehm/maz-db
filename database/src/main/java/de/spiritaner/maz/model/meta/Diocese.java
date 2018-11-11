package de.spiritaner.maz.model.meta;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * @author Florian Böhm
 * @version 0.0.1
 */
@Entity
@Audited(targetAuditMode = NOT_AUDITED)
@NamedQueries({
        @NamedQuery(name = "Diocese.findAll", query = "SELECT d FROM Diocese d ORDER BY d.description"),
})
public class Diocese extends MetaClass {

	public Diocese(){

	}

	public static Diocese createEmpty() {
		Diocese result = new Diocese();
		result.setDescription("-/-");
		result.setId(-1L);
		return result;
	}
}

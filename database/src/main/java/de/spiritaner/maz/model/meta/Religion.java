package de.spiritaner.maz.model.meta;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Audited(targetAuditMode = NOT_AUDITED)
@NamedQueries({
		  @NamedQuery(name = "Religion.findAll", query = "SELECT r FROM Religion r"),
})
public class Religion extends MetaClass {

	public static Religion createEmpty() {
		Religion result = new Religion();
		result.setDescription("-/-");
		result.setId(-1L);
		return result;
	}
}

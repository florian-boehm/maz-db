package de.spiritaner.maz.model.meta;

import de.spiritaner.maz.model.Approval;
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
		  @NamedQuery(name = "ApprovalType.findAll", query = "SELECT at FROM ApprovalType at"),
		  @NamedQuery(name = "ApprovalType.findAllWithIdGreaterThanThree", query = "SELECT at FROM ApprovalType at WHERE at.id>3"),
})
public class ApprovalType extends MetaClass {

	private List<Approval> approvals;

	@OneToMany(mappedBy = "approvalType", fetch = FetchType.LAZY)
	public List<Approval> getApprovals() {
		return approvals;
	}

	public void setApprovals(List<Approval> approvals) {
		this.approvals = approvals;
	}
}

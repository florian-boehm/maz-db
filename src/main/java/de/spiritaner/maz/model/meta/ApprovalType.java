package de.spiritaner.maz.model.meta;

import de.spiritaner.maz.model.Approval;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;
import java.util.List;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
public class ApprovalType extends MetaClass {

	private List<Approval> approvals;

	@OneToMany(mappedBy = "approvalType", fetch = FetchType.LAZY)
	public List<Approval> getApprovals() { return approvals; }
	public void setApprovals(List<Approval> approvals) { this.approvals = approvals; }
}

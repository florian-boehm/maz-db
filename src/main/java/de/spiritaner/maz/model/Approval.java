package de.spiritaner.maz.model;

import de.spiritaner.maz.model.meta.ApprovalType;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@Audited
public class Approval {

	private LongProperty id;

	private Person person;
	private ApprovalType approvalType;
	private Boolean isApproved;

	public Approval() {
		id = new SimpleLongProperty();
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id.get();
	}
	public void setId(Long id) {
		this.id.set(id);
	}
	public LongProperty idProperty() { return id; }

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="personId", nullable = false)
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "approvalTypeId", nullable = false)
	public ApprovalType getApprovalType() {
		return approvalType;
	}
	public void setApprovalType(ApprovalType approvalType) {
		this.approvalType = approvalType;
	}

	@Column(nullable = false)
	public Boolean getApproved() {
		return isApproved;
	}
	public void setApproved(Boolean approved) {
		isApproved = approved;
	}
}

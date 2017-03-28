package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.approval.ApprovalEditorDialogController;
import de.spiritaner.maz.model.meta.ApprovalType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Entity
@Audited
@Identifiable.Annotation(editorDialogClass = ApprovalEditorDialogController.class, identifiableName = "Einwilligung")
public class Approval implements Identifiable {

	private LongProperty id;

	private Person person;
	private ApprovalType approvalType;
	private BooleanProperty approved;

	public Approval() {
		approved = new SimpleBooleanProperty();
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
	public boolean isApproved() {
		return approved.get();
	}
	public BooleanProperty approvedProperty() {
		return approved;
	}
	public void setApproved(boolean approved) {
		this.approved.set(approved);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Approval) && (((Approval) obj).getId().equals(this.getId()));
	}
}

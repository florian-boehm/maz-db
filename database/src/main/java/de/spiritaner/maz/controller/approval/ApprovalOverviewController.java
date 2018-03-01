package de.spiritaner.maz.controller.approval;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.model.Approval;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.meta.ApprovalType;
import de.spiritaner.maz.view.dialog.RemoveDialog;
import de.spiritaner.maz.view.renderer.BooleanTableCell;
import de.spiritaner.maz.view.renderer.MetaClassTableCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

public class ApprovalOverviewController extends OverviewController<Approval> {

	public TableColumn<Approval, ApprovalType> approvalTypeColumn;
	public TableColumn<Approval, Boolean> approvedColumn;

	public ObjectProperty<Person> person = new SimpleObjectProperty<>();

	public ApprovalOverviewController() {
		super(Approval.class, Boolean.TRUE);
	}

	@Override
	protected void preCreate(Approval newApproval) {
		newApproval.setPerson(person.get());
	}

	@Override
	protected void postRemove(Approval obsoleteEntity) {
		person.get().getApprovals().remove(obsoleteEntity);
	}

	@Override
	protected Collection<Approval> preLoad(EntityManager em) {
		if(person != null) {
			Hibernate.initialize(person.get().getApprovals());
			return FXCollections.observableArrayList(person.get().getApprovals());
		} else {
			return FXCollections.emptyObservableList();
		}
	}

	@Override
	protected String getLoadingText() {
		return guiText.getString("loading") + " " + guiText.getString("approvals") + " ...";
	}

	@Override
	protected void handleException(RollbackException e, Approval approval) {
		// TODO Extract strings
		RemoveDialog.showFailureAndWait(guiText.getString("approval"),"Einwilligung von '"+approval.getPerson().getFullName()+"' zu '"+approval.getApprovalType().getDescription()+"'", e);
	}

	@Override
	protected void postInit() {
		approvalTypeColumn.setCellValueFactory(cellData -> cellData.getValue().approvalTypeProperty());
		approvedColumn.setCellValueFactory(cellData -> cellData.getValue().approvedProperty());

		approvalTypeColumn.setCellFactory(column -> new MetaClassTableCell<>());
		approvedColumn.setCellFactory(column -> new BooleanTableCell<>());
	}

	@Override
	protected boolean isRemoveButtonDisabled(Approval oldVal, Approval newVal) {
		return newVal == null || (newVal.getApprovalType() != null && newVal.getApprovalType().getId() <= 3);
	}
}

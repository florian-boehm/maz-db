package de.spiritaner.maz.controller.approval;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.view.dialog.RemoveDialog;
import de.spiritaner.maz.model.Approval;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.meta.ApprovalType;
import de.spiritaner.maz.view.renderer.BooleanTableCell;
import de.spiritaner.maz.view.renderer.MetaClassTableCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

public class ApprovalOverviewController extends OverviewController<Approval> {

	@FXML
	private TableColumn<Approval, ApprovalType> approvalTypeColumn;
	@FXML
	private TableColumn<Approval, Boolean> approvedColumn;
	@FXML
	private TableColumn<Approval, Long> idColumn;

	private Person person;

	public ApprovalOverviewController() {
		super(Approval.class, Boolean.TRUE);
	}

	@Override
	protected void preCreate(Approval newApproval) {
		newApproval.setPerson(person);
	}

	@Override
	protected void postRemove(Approval obsoleteEntity) {
		person.getApprovals().remove(obsoleteEntity);
	}

	@Override
	protected Collection<Approval> preLoad(EntityManager em) {
		if(person != null) {
			Hibernate.initialize(person.getApprovals());
			return FXCollections.observableArrayList(person.getApprovals());
		} else {
			return FXCollections.emptyObservableList();
		}
	}

	@Override
	protected String getLoadingText() {
		return "Lade Einwilligungen ...";
	}

	@Override
	protected void handleException(RollbackException e, Approval approval) {
		RemoveDialog.showFailureAndWait("Einwilligung","Einwilligung von '"+approval.getPerson().getFullName()+"' zu '"+approval.getApprovalType().getDescription()+"'", e);
	}

	@Override
	protected void postInit() {
		approvalTypeColumn.setCellValueFactory(cellData -> cellData.getValue().approvalTypeProperty());
		approvedColumn.setCellValueFactory(cellData -> cellData.getValue().approvedProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

		approvalTypeColumn.setCellFactory(column -> new MetaClassTableCell<>());
		approvedColumn.setCellFactory(column -> new BooleanTableCell<>());
	}

	@Override
	protected boolean isRemoveButtonDisabled(Approval oldVal, Approval newVal) {
		return newVal == null || (newVal.getApprovalType() != null && newVal.getApprovalType().getId() <= 3);
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}

package de.spiritaner.maz.controller.approval;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.dialog.ExceptionDialog;
import de.spiritaner.maz.model.Approval;
import de.spiritaner.maz.model.Person;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

public class ApprovalOverviewController extends OverviewController<Approval> {

	@FXML
	private TableColumn<Approval, String> approvalTypeColumn;
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
	protected void postCreate(Approval obj) {

	}

	@Override
	protected void preEdit(Approval object) {

	}

	@Override
	protected void preRemove(Approval obsoleteEntity, EntityManager em) {

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
	protected void postLoad(Collection<Approval> loadedObjs) {

	}

	@Override
	protected String getLoadingText() {
		return "Lade Einwilligungen ...";
	}

	@Override
	protected void handleException(RollbackException e) {
		ExceptionDialog.show(e);
	}

	@Override
	protected void preInit() {

	}

	@Override
	protected void postInit() {
		approvalTypeColumn.setCellValueFactory(cellData -> cellData.getValue().getApprovalType().descriptionProperty());
		approvedColumn.setCellValueFactory(cellData -> cellData.getValue().approvedProperty());
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

		approvedColumn.setCellFactory(column -> {
			return new TableCell<Approval, Boolean>() {
				@Override
				protected void updateItem(Boolean item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
						setStyle("kA");
					} else {
						setText((item) ? "Ja" : "Nein");
					}
				}
			};
		});
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}

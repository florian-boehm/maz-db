package de.spiritaner.maz.controller.approval;

import de.spiritaner.maz.controller.meta.ApprovalTypeOverviewController;
import de.spiritaner.maz.model.Approval;
import de.spiritaner.maz.model.meta.ApprovalType;
import de.spiritaner.maz.util.database.DataDatabase;
import de.spiritaner.maz.util.factory.MetaClassListCell;
import de.spiritaner.maz.util.validator.ComboBoxValidator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.apache.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class ApprovalEditorController implements Initializable {

	final static Logger logger = Logger.getLogger(ApprovalEditorController.class);

	@FXML
	private Button addNewApprovalTypeButton;
	@FXML
	private ToggleSwitch approvedToggleSwitch;
	@FXML
	private ComboBox<ApprovalType> approvalTypeComboBox;

	private boolean readOnly = false;
	private ComboBoxValidator<ApprovalType> approvalTypeValidator;

	public void initialize(URL location, ResourceBundle resources) {
		approvalTypeValidator = new ComboBoxValidator<>(approvalTypeComboBox).fieldName("Einwilligungsart").isSelected(true).validateOnChange();

		approvalTypeComboBox.setCellFactory(column -> new MetaClassListCell<>());
		approvalTypeComboBox.setButtonCell(new MetaClassListCell<>());

		loadApprovalType();
	}

	public void setAll(Approval approval) {
		approvedToggleSwitch.setSelected(approval.isApproved());
		approvalTypeComboBox.setValue(approval.getApprovalType());
	}

	public Approval getAll(Approval approval) {
		if (approval == null) approval = new Approval();
		approval.setApproved(approvedToggleSwitch.isSelected());
		approval.setApprovalType(approvalTypeComboBox.getValue());
		return approval;
	}

	public void setReadonly(boolean readonly) {
		this.readOnly = readonly;
		approvedToggleSwitch.setDisable(readonly);
		approvalTypeComboBox.setDisable(readonly);
		addNewApprovalTypeButton.setDisable(readonly);
	}

	public void loadApprovalType() {
		EntityManager em = DataDatabase.getFactory().createEntityManager();
		Collection<ApprovalType> result = em.createNamedQuery("ApprovalType.findAllWithIdGreaterThanThree", ApprovalType.class).getResultList();

		ApprovalType selectedBefore = approvalTypeComboBox.getValue();
		approvalTypeComboBox.getItems().clear();
		approvalTypeComboBox.getItems().addAll(FXCollections.observableArrayList(result));
		approvalTypeComboBox.setValue(selectedBefore);
	}

	public boolean isValid() {
		boolean approvalTypeValid = approvalTypeValidator.validate();

		return approvalTypeValid;
	}

	public void addNewApprovalType(ActionEvent actionEvent) {
		new ApprovalTypeOverviewController().create(actionEvent);

		loadApprovalType();
	}

	public boolean isReadOnly() {
		return readOnly;
	}
}

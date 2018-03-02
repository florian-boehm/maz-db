package de.spiritaner.maz.controller.approval;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.meta.ApprovalTypeOverviewController;
import de.spiritaner.maz.model.Approval;
import de.spiritaner.maz.model.meta.ApprovalType;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.ComboBoxValidator;
import de.spiritaner.maz.view.binding.AutoBinder;
import de.spiritaner.maz.view.component.BindableComboBox;
import de.spiritaner.maz.view.component.BindableToggleSwitch;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class ApprovalEditorController extends EditorController {

	final static Logger logger = Logger.getLogger(ApprovalEditorController.class);

	public ObjectProperty<Approval> approval = new SimpleObjectProperty<>();

	public Button addNewApprovalTypeButton;
	public BindableToggleSwitch approvedToggleSwitch;
	public BindableComboBox<ApprovalType> approvalTypeComboBox;

	private ComboBoxValidator<ApprovalType> approvalTypeValidator;

	public void initialize(URL location, ResourceBundle resources) {
		AutoBinder ab = new AutoBinder(this);
		approval.addListener((observableValue, oldValue, newValue) -> ab.rebindAll());

		// TODO Extract strings
		approvalTypeValidator = new ComboBoxValidator<>(approvalTypeComboBox).fieldName("Einwilligungsart").isSelected(true).validateOnChange();

		loadApprovalType();
	}

	private void loadApprovalType() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<ApprovalType> result = em.createNamedQuery("ApprovalType.findAllWithIdGreaterThanThree", ApprovalType.class).getResultList();

		approvalTypeComboBox.populate(result, null);
	}

	public void addNewApprovalType(ActionEvent actionEvent) {
		new ApprovalTypeOverviewController().create(actionEvent);

		loadApprovalType();
	}

	public boolean isValid() {
		boolean approvalTypeValid = approvalTypeValidator.validate();

		return approvalTypeValid;
	}
}

package de.spiritaner.maz.controller.approval;

import de.spiritaner.maz.controller.EditorController;
import de.spiritaner.maz.controller.meta.ApprovalTypeOverviewController;
import de.spiritaner.maz.model.Approval;
import de.spiritaner.maz.model.meta.ApprovalType;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.validator.PopOverVisitor;
import de.spiritaner.maz.util.validator.Selected;
import de.spiritaner.maz.view.component.BindableComboBox;
import de.spiritaner.maz.view.component.BindableToggleSwitch;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class ApprovalEditorController extends EditorController {

	public ObjectProperty<Approval> approval = new SimpleObjectProperty<>();

	public Button addNewApprovalTypeButton;
	public BindableToggleSwitch approvedToggleSwitch;
	public BindableComboBox<ApprovalType> approvalTypeComboBox;

	public void initialize(URL location, ResourceBundle resources) {
		// Bind all bindable fields to the bindable property
		autoBinder.register(this);
		approval.addListener((observableValue, oldValue, newValue) -> autoBinder.rebindAll());

		// Change the validator visitor to PopOver and add Validations as well as change listeners
		autoValidator.visitor = new PopOverVisitor();
		autoValidator.add(new Selected(approvalTypeComboBox, guiText.getString("approval_type")));
		autoValidator.validateOnChange(approvalTypeComboBox);

		loadApprovalType();
	}

	private void loadApprovalType() {
		EntityManager em = CoreDatabase.getFactory().createEntityManager();
		Collection<ApprovalType> result = em.createNamedQuery("ApprovalType.findAll", ApprovalType.class).getResultList();

		approvalTypeComboBox.populate(result, null);
	}

	public void addNewApprovalType(ActionEvent actionEvent) {
		new ApprovalTypeOverviewController().create(actionEvent);

		loadApprovalType();
	}
}

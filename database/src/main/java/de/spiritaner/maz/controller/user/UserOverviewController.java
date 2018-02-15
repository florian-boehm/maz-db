package de.spiritaner.maz.controller.user;

import de.spiritaner.maz.controller.OverviewController;
import de.spiritaner.maz.model.User;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.database.UserDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

@OverviewController.Annotation(fxmlFile = "/fxml/user/user_overview.fxml", objDesc = "Benutzer")
public class UserOverviewController extends OverviewController<User> {

	final static Logger logger = Logger.getLogger(UserOverviewController.class);

	@FXML
	private TableColumn<User, String> usernameColumn;

	public UserOverviewController() {
		super(User.class, false);
	}

	@Override
	protected void postInit() {
		usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());

		getCreateButton().setDisable(isCreateButtonDisabled(null, null));
		getEditButton().setDisable(isEditButtonDisabled(null, getTable().getSelectionModel().getSelectedItem()));
		getRemoveButton().setDisable(isRemoveButtonDisabled(null, getTable().getSelectionModel().getSelectedItem()));
	}

	@Override
	protected Collection<User> preLoad(EntityManager em) {
		return UserDatabase.findAll();
	}

	@Override
	protected String getLoadingText() {
		return "Lade Benutzer ...";
	}

	@Override
	protected void handleException(RollbackException e, User selectedObj) {
		logger.error(e);
	}

	@Override
	protected void preRemove(User obsoleteEntity, EntityManager em) {
		UserDatabase.removeUser(obsoleteEntity);
	}


	@Override
	protected boolean isRemoveButtonDisabled(User oldVal, User newVal) {
		return !(CoreDatabase.getCurrentUser().getUsername().equals("admin") && (newVal == null || !newVal.getUsername().equals("admin")));
	}

	@Override
	protected boolean isEditButtonDisabled(User oldVal, User newVal) {
		return !(CoreDatabase.getCurrentUser().getUsername().equals("admin") || (newVal != null && CoreDatabase.getCurrentUser().getUsername().equals(newVal.getUsername())));
	}

	@Override
	protected boolean isCreateButtonDisabled(User oldVal, User newVal) {
		return !CoreDatabase.getCurrentUser().getUsername().equals("admin");
	}
}

package de.spiritaner.maz.controller;

import de.spiritaner.maz.model.User;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.database.UserDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.Collection;

@OverviewController.Annotation(fxmlFile = "/fxml/user_dialog.fxml", objDesc = "Benutzer")
public class UserOverviewController extends OverviewController<User> {

	@FXML
	private TableColumn<User, String> usernameColumn;

	public UserOverviewController() {
		super(User.class, false);
	}

	@Override
	protected void postInit() {
		usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
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
	protected boolean isRemoveButtonDisabled(User oldVal, User newVal) {
		return !CoreDatabase.getCurrentUser().getUsername().equals("admin");
	}

	@Override
	protected boolean isEditButtonDisabled(User oldVal, User newVal) {
		return !(CoreDatabase.getCurrentUser().getUsername().equals("admin") || CoreDatabase.getCurrentUser().getUsername().equals(newVal.getUsername()));
	}

	@Override
	protected boolean isCreateButtonDisabled(User oldVal, User newVal) {
		return !CoreDatabase.getCurrentUser().getUsername().equals("admin");
	}

	public void close(ActionEvent actionEvent) {
		getStage().close();
	}
}

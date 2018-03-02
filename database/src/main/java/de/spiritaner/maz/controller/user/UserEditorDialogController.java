package de.spiritaner.maz.controller.user;


import de.spiritaner.maz.controller.EditorDialogController;
import de.spiritaner.maz.model.User;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.database.UserDatabase;
import de.spiritaner.maz.view.dialog.EditorDialog;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;

import java.util.Optional;

@EditorDialog.Annotation(fxmlFile = "/fxml/user/user_editor_dialog.fxml", objDesc = "$user")
public class UserEditorDialogController extends EditorDialogController<User> {

	final static Logger logger = Logger.getLogger(UserEditorDialogController.class);

	public PasswordField adminPasswordField;
	public Text titleText;
	public Button saveUserButton;
	public GridPane userEditor;
	public UserEditorController userEditorController;
	public Label errorLabel;
	public Label adminPasswordLabel;

	public void saveUser(ActionEvent actionEvent) {
		Platform.runLater(() -> {
			boolean validation = userEditorController.isValid();

			if (validation) {
				User admin = null;

				if(isAdmin()) {
					admin = new User("admin", adminPasswordField.getText());
				} else {
					admin = new User(getIdentifiable().getUsername(), adminPasswordField.getText());
				}

				if (UserDatabase.validateLogin(admin, false)) {
					userEditorController.getAll(getIdentifiable());

					Optional<User> result = (getIdentifiable().getId() != 0L) ?
							UserDatabase.updateUser(getIdentifiable(), admin, userEditorController.isPasswordOverwritten()) :
							UserDatabase.createUser(getIdentifiable(), admin);

					if (result.isPresent()) {
						setResult(result);
						requestClose();
					}
				} else {
					if(isAdmin()) {
						setErrorMsg("Administrator-Passwort ist falsch!");
					} else {
						setErrorMsg("Bisheriges Passwort ist falsch!");
					}
				}
			}
		});
	}

	@Override
	public void setIdentifiable(User user) {
		super.setIdentifiable(user);

		if (user != null) {
			userEditorController.setAll(user);

			if (user.getId() != 0L) {
				titleText.setText("Benutzer bearbeiten");
				saveUserButton.setText("Speichern");
			} else {
				titleText.setText("Benutzer anlegen");
				saveUserButton.setText("Anlegen");
			}

			if (isAdmin() && user.getUsername() != null && user.getUsername().equals("admin") || !isAdmin()) {
				adminPasswordLabel.setText("Bisheriges Passwort:");
			} else {
				adminPasswordLabel.setText("Administrator-Passwort:");
			}
		}
	}

	public void setErrorMsg(final String errorMsg) {
		Platform.runLater(() -> {
			if (errorMsg == null) {
				errorLabel.setVisible(false);
				errorLabel.setManaged(false);
				errorLabel.setStyle("-fx-text-fill: #B80024; -fx-font-weight: bold");
			} else {
				errorLabel.setVisible(true);
				errorLabel.setManaged(true);
				errorLabel.setStyle("-fx-text-fill: #B80024; -fx-font-weight: bold");
				errorLabel.setText(errorMsg);
			}
		});
	}

	private boolean isAdmin() {
		return CoreDatabase.getCurrentUser().getUsername().toLowerCase().equals("admin");
	}
}

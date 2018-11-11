package de.spiritaner.maz.controller.user;

import de.spiritaner.maz.model.User;
import de.spiritaner.maz.util.validator.TextValidation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class UserEditorController implements Initializable {

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private PasswordField passwordCheckField;

	private TextValidation usernameFieldValidator;
	private TextValidation passwordFieldValidator;
	private TextValidation passwordCheckFieldValidator;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		usernameFieldValidator = TextValidation.create(usernameField, "Benutzername").min(3).max(16).notEmpty(true).removeAll(" ").justText().validateOnChange();
		passwordFieldValidator = TextValidation.create(passwordField, "Passwort").min(8).notEmpty(true).removeAll(" ").validateOnChange();
		passwordCheckFieldValidator = TextValidation.create(passwordCheckField, "Wiederholtes Passwort").equals(passwordField).validateOnChange();
	}

	public void setAll(User user) {
		usernameField.setText(user.getUsername());

		if(user.getUsername() != null && user.getUsername().equals("admin")) {
			usernameField.setDisable(true);
		}
	}

	public User getAll(User user) {
		if(user == null) user = new User();
		user.setUsername(usernameField.getText());
		user.setPassword(passwordField.getText());

		return user;
	}

	public void setReadonly(boolean readonly) {
		usernameField.setDisable(readonly);
		passwordField.setDisable(readonly);
		passwordCheckField.setDisable(readonly);
	}

	public boolean isValid() {
		boolean usernameValid = usernameFieldValidator.isValid();

		// Just if the user wants to override the password, we have to check if the newly entered password is valid
		boolean passwordValid = (!isPasswordOverwritten()) || passwordFieldValidator.isValid();
		boolean passwordCheckValid = (!isPasswordOverwritten()) || passwordCheckFieldValidator.isValid();

		return usernameValid && passwordValid && passwordCheckValid;
	}

	public boolean isPasswordOverwritten() {
		return !passwordField.getText().isEmpty();
	}
}

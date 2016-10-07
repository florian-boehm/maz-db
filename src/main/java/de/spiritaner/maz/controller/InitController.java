package de.spiritaner.maz.controller;

import de.spiritaner.maz.dialog.LoginDialog;
import de.spiritaner.maz.util.TextValidator;
import de.spiritaner.maz.util.UserDatabase;
import de.spiritaner.maz.view.MainView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
public class InitController implements Initializable {

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private PasswordField passwordCheckField;

	@FXML
	private Button setupButton;

	private TextValidator usernameFieldValidator;
	private TextValidator passwordFieldValidator;
	private TextValidator passwordFieldCheckValidator;

	private Stage stage;

	public void initialize(URL location, ResourceBundle resources) {
		usernameFieldValidator = TextValidator.create(usernameField).fieldName("Benutzername").min(3).max(16).notEmpty(true).removeAll(" ").justText().textChanged();
		passwordFieldValidator = TextValidator.create(passwordField).fieldName("Passwort").min(8).notEmpty(true).removeAll(" ").textChanged();
		passwordFieldCheckValidator = TextValidator.create(passwordCheckField).fieldName("Wiederholtes Passwort").equals(passwordField).textChanged();
	}

	public void setupDatabase(ActionEvent actionEvent) {
		boolean usernameValid = usernameFieldValidator.validate();
		boolean passwordValid = passwordFieldValidator.validate();
		boolean passwordCheckValid = passwordFieldCheckValidator.validate();

		if(usernameValid && passwordValid && passwordCheckValid) {
			UserDatabase.createFirstUser(usernameField.getText(), passwordField.getText());

			if(UserDatabase.isPopulated() && stage != null) {
				stage.hide();
				LoginDialog.showWaitAndExitOnFailure(stage);
			}
		}
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}

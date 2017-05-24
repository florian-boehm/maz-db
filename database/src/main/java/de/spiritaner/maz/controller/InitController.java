package de.spiritaner.maz.controller;

import de.spiritaner.maz.dialog.LoginDialog;
import de.spiritaner.maz.util.Settings;
import de.spiritaner.maz.util.database.UserDatabase;
import de.spiritaner.maz.util.validator.TextValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Florian Schwab
 */
public class InitController implements Initializable {

	final static Logger logger = Logger.getLogger(InitController.class);

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
		usernameFieldValidator = TextValidator.create(usernameField).fieldName("Benutzername").min(3).max(16).notEmpty(true).removeAll(" ").justText().validateOnChange();
		passwordFieldValidator = TextValidator.create(passwordField).fieldName("Passwort").min(8).notEmpty(true).removeAll(" ").validateOnChange();
		passwordFieldCheckValidator = TextValidator.create(passwordCheckField).fieldName("Wiederholtes Passwort").equals(passwordField).validateOnChange();
	}

	public void setupDatabase(ActionEvent actionEvent) {
		boolean usernameValid = usernameFieldValidator.validate();
		boolean passwordValid = passwordFieldValidator.validate();
		boolean passwordCheckValid = passwordFieldCheckValidator.validate();

		if(usernameValid && passwordValid && passwordCheckValid) {
			UserDatabase.createFirstUser(usernameField.getText(), passwordField.getText());

			try {
				if(UserDatabase.isPopulated() && stage != null) {
					// Create the version file in the new created database directory
					new File(Settings.get("database.path","./dbfiles/")+ResourceBundle.getBundle("lang.gui").getString("version")+".version").createNewFile();

					stage.setScene(new Scene(new Label("Bitte warten ...")));
					stage.setTitle("Bitte warten ...");
					stage.sizeToScene();

					LoginDialog.showWaitAndExitOnFailure(stage);
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}

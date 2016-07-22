package de.spiritaner.maz.dialog;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.util.Optional;

/**
 * Copyleft some parts from http://code.makery.ch/blog/javafx-dialogs-official/
 *
 * @author Florian Schwab
 * @version 0.0.1
 */
public class LoginDialog {

	final static Logger logger = Logger.getLogger(LoginDialog.class);

	public static Optional<Pair<String,String>> show() {
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("MAZ-Datenbank");
		dialog.setHeaderText("Bitte melden Sie sich an");

		// Set the icon (must be included in the project).
		dialog.setGraphic(new ImageView(LoginDialog.class.getClass().getResource("/img/login_32.png").toString()));

		// Set the button types.
		ButtonType loginButtonType = new ButtonType("Anmelden", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));

		TextField username = new TextField();
		username.setPromptText("Benutzername");
		PasswordField password = new PasswordField();
		password.setPromptText("Passwort");

		grid.add(new Label("Benutzername:"), 0, 0);
		grid.add(username, 1, 0);
		grid.add(new Label("Passwort:"), 0, 1);
		grid.add(password, 1, 1);

		// Enable/Disable login button depending on whether a username was entered.
		Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		username.textProperty().addListener((observable, oldValue, newValue) -> {
			loginButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the username field by default.
		Platform.runLater(() -> username.requestFocus());

		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {
				return new Pair<>(username.getText(), password.getText());
			}
			return null;
		});


		Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(new Image(LoginDialog.class.getClass().getResource("/img/database_64.png").toString()));

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(usernamePassword -> {
			System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
		});

		return result;
	}
}

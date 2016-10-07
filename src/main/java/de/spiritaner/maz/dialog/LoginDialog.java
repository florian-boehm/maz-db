package de.spiritaner.maz.dialog;

import de.spiritaner.maz.util.UserDatabase;
import de.spiritaner.maz.view.MainView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import org.hibernate.annotations.common.util.impl.Log;

import java.util.Optional;

/**
 * Copyleft some parts from http://code.makery.ch/blog/javafx-dialogs-official/
 *
 * @author Florian Schwab
 * @version 0.0.1
 */
public class LoginDialog {

	final static Logger logger = Logger.getLogger(LoginDialog.class);

	public static boolean showAndWait()  {
		Optional<Pair<String, String>> result;
		boolean loginSuccess = false;
		String msgInDialog = null;

		do {
			result = LoginDialog.showAndWait(msgInDialog);

			if(result.isPresent()) {
				loginSuccess = UserDatabase.testLogin(result.get().getKey(), result.get().getValue());
			}

			if(!loginSuccess) {
				msgInDialog = "Der Benutzername oder das Passwort ist falsch!";
			}
		} while (result.isPresent() && !loginSuccess);

		return loginSuccess;
	}

	private static Optional<Pair<String,String>> showAndWait(String msgInDialog) {
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Anmeldung - MaZ-Datenbank");

		BorderPane borderPane = new BorderPane();
		VBox vboxAlignCenter = new VBox();
		vboxAlignCenter.setAlignment(Pos.CENTER);
		vboxAlignCenter.getChildren().add(new ImageView(LoginDialog.class.getClass().getResource("/img/logo.png").toString()));
		vboxAlignCenter.setPadding(new Insets(10,0,20,0));
		borderPane.setTop(vboxAlignCenter);

		// Set the icon (must be included in the project).
//		dialog.setGraphic(new ImageView(LoginDialog.class.getClass().getResource("/img/logo.png").toString()));

		// Set the button types.
		ButtonType loginButtonType = new ButtonType("Anmelden", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
//		grid.setPadding(new Insets(10, 10, 10, 10));

		TextField username = new TextField();
		username.setPromptText("Benutzername");
		PasswordField password = new PasswordField();
		password.setPromptText("Passwort");

		grid.add(new Separator(),0,0,2,1);
		grid.add(new Label("Benutzername:"), 0, 1);
		grid.add(username, 1, 1);
		grid.add(new Label("Passwort:"), 0, 2);
		grid.add(password, 1, 2);

		if(msgInDialog != null && !msgInDialog.isEmpty()) {
			Label msgLabel = new Label(msgInDialog);
			msgLabel.setStyle("-fx-text-fill: #B80024; -fx-font-weight: bold");
			grid.add(msgLabel,0,3,2,1);
		}

		borderPane.setCenter(grid);

		// Enable/Disable login button depending on whether a username was entered.
		Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		username.textProperty().addListener((observable, oldValue, newValue) -> {
			loginButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(borderPane);

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
//		dialog.getDialogPane().setPadding(new Insets(10));
		dialogStage.getIcons().add(new Image(LoginDialog.class.getClass().getResource("/img/login_32.png").toString()));

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(usernamePassword -> {
			System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
		});

		return result;
	}

	public static void showWaitAndExitOnFailure(Stage stage) {
		boolean loginSuccess = LoginDialog.showAndWait();

		if(loginSuccess) {
			MainView.populateStage(stage);
			stage.show();
		} else {
			Platform.exit();
		}
	}
}

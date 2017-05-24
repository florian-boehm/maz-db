package de.spiritaner.maz.dialog;

import de.spiritaner.maz.util.Settings;
import de.spiritaner.maz.util.database.UserDatabase;
import de.spiritaner.maz.view.MainView;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

/**
 * Copyleft some parts from http://code.makery.ch/blog/javafx-dialogs-official/
 *
 * This dialog shows a username field, password field and selector for the current database directory.
 *
 * @author Florian Schwab
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
				loginSuccess = UserDatabase.validateLogin(result.get().getKey(), result.get().getValue());
			}

			if(!loginSuccess) {
				msgInDialog = ResourceBundle.getBundle("lang.gui").getString("login_failed");
			}
		} while (result.isPresent() && !loginSuccess);

		return loginSuccess;
	}

	private static Optional<Pair<String,String>> showAndWait(final String msgInDialog) {
		final ResourceBundle guiText = ResourceBundle.getBundle("lang.gui");
		final Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle(guiText.getString("login_dialog_title"));
		dialog.setResizable(true);

		BorderPane borderPane = new BorderPane();
		VBox vboxAlignCenter = new VBox();
		vboxAlignCenter.setAlignment(Pos.CENTER);
		vboxAlignCenter.getChildren().add(new ImageView(LoginDialog.class.getClass().getResource("/img/logo.png").toString()));
		vboxAlignCenter.setPadding(new Insets(10,0,10,0));
		vboxAlignCenter.getChildren().add(new Label("Version "+guiText.getString("version")));
		vboxAlignCenter.setSpacing(10.0);
		borderPane.setTop(vboxAlignCenter);

		final Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
		//	dialog.getDialogPane().setPadding(new Insets(10));
		dialogStage.getIcons().add(new Image(LoginDialog.class.getClass().getResource("/img/login_32.png").toString()));

		// Set the button types.
		ButtonType loginButtonType = new ButtonType(guiText.getString("login"), ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		//	grid.setPadding(new Insets(10, 10, 10, 10));

		grid.getColumnConstraints().add(new ColumnConstraints(80, 80, 80, Priority.NEVER, HPos.LEFT, true));
		grid.getColumnConstraints().add(new ColumnConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, Priority.ALWAYS, HPos.LEFT, true));

		TextField username = new TextField();
		username.setPromptText(guiText.getString("username"));
		PasswordField password = new PasswordField();
		password.setPromptText(guiText.getString("password"));

		final ComboBox<File> databaseSelector = new ComboBox<>();
		databaseSelector.setCellFactory(param -> new DatabaseFolderRenderer());
		databaseSelector.setButtonCell(new DatabaseFolderRenderer());

		final Button openButton = new Button(guiText.getString("others"));

		searchDbFilesFolder(new File(Settings.get("database.parent", "./")), databaseSelector);
		dialogStage.sizeToScene();
		openButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				final DirectoryChooser directoryChooser = new DirectoryChooser();
				directoryChooser.setInitialDirectory(new File(Settings.get("database.parent", "./")));

				final File selectedDirectory = directoryChooser.showDialog(new Stage());

				if(selectedDirectory != null && selectedDirectory.exists()) {
					Settings.set("database.parent", selectedDirectory.getAbsolutePath());
					searchDbFilesFolder(selectedDirectory, databaseSelector);
					dialogStage.sizeToScene();
				}
			}
		});

		BorderPane innerBorderPane = new BorderPane();
		GridPane.setVgrow(databaseSelector, Priority.ALWAYS);
		databaseSelector.setMaxWidth(Double.MAX_VALUE);
		innerBorderPane.setCenter(databaseSelector);
		innerBorderPane.setRight(openButton);

		Separator sep = new Separator();
		sep.setPrefWidth(USE_COMPUTED_SIZE);
		sep.setMaxWidth(Double.MAX_VALUE);
		GridPane.setVgrow(sep, Priority.ALWAYS);
		grid.add(sep,0,0,2147483647,1);
		grid.add(new Label(guiText.getString("username") + ":"), 0, 1);
		grid.add(username, 1, 1);
		grid.add(new Label(guiText.getString("password") + ":"), 0, 2);
		grid.add(password, 1, 2);
		grid.add(new Label(guiText.getString("database") + ":"), 0, 3);
		grid.add(innerBorderPane, 1,3);

		if(msgInDialog != null && !msgInDialog.isEmpty()) {
			Label msgLabel = new Label(msgInDialog);
			msgLabel.setStyle("-fx-text-fill: #B80024; -fx-font-weight: bold");
			grid.add(msgLabel,0,4,2,1);
		}

		borderPane.setCenter(grid);

		// Enable/Disable login button depending on whether a username was entered.
		final Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		username.textProperty().addListener((observable, oldValue, newValue) -> {
			loginButton.setDisable(newValue.trim().isEmpty() || databaseSelector.getSelectionModel().getSelectedItem() == null);
		});

		databaseSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
			loginButton.setDisable(username.getText().trim().isEmpty() || databaseSelector.getSelectionModel().getSelectedItem() == null);
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

		dialogStage.setOnShown(event -> {
			dialogStage.sizeToScene();
			dialogStage.setMaxHeight(dialogStage.getHeight());
			dialogStage.setMinHeight(dialogStage.getHeight());
			dialogStage.setMinWidth(dialogStage.getWidth());
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(usernamePassword -> {
					  logger.info("User '" + usernamePassword.getKey() + " logged into database!");
				  });

		logger.info("Selected database directory: " + databaseSelector.getSelectionModel().getSelectedItem().getAbsolutePath()+"/");
		Settings.set("database.path", databaseSelector.getSelectionModel().getSelectedItem().getAbsolutePath()+"/");

		return result;
	}

	private static void searchDbFilesFolder(File workingDirectory, ComboBox<File> databaseSelector) {
		final String version = ResourceBundle.getBundle("lang.gui").getString("version");
		File[] dbDirs = workingDirectory.listFiles((current, name) -> new File(current, name).isDirectory() && name.startsWith("dbfiles") && new File(current, name+"/"+version+".version").exists());

		databaseSelector.getItems().clear();

		if(dbDirs != null && dbDirs.length == 0)
			databaseSelector.getItems().add(null);
		else
			databaseSelector.getItems().addAll(dbDirs);

		databaseSelector.getSelectionModel().clearAndSelect(0);
	}

	/**
	 * This method spawns the main window or will exit if the login was not successfull
	 *
	 * @param stage The primary stage of the javafx application
	 */
	public static void showWaitAndExitOnFailure(Stage stage) {
		boolean loginSuccess = LoginDialog.showAndWait();

		if(loginSuccess) {
			MainView.populateStage(stage);
			stage.show();
		} else {
			Platform.exit();
		}
	}

	/**
	 * This class helps rendering the possible dbfiles folders in the combobox
	 */
	static class DatabaseFolderRenderer extends ListCell<File> {

		final DateTimeFormatter fromFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
		final DateTimeFormatter toFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
		final ResourceBundle guiText = ResourceBundle.getBundle("lang.gui");

		@Override
		public void updateItem(File item, boolean empty) {
			super.updateItem(item, empty);

			if (item == null || empty) {
				//setText(null);
				//setStyle("");
				setText(guiText.getString("none"));
			} else {
				if(item.getName().equals("dbfiles")) {
					setText(guiText.getString("current"));
				} else if(item.getName().contains("-")){
					String name = item.getName();
					String time = name.substring(name.indexOf("-")+1);
					setText(guiText.getString("backup") + " ("+toFormatter.format(fromFormatter.parse(time))+")");
				} else {
					setText(item.getName());
				}
			}
		}
	}
}

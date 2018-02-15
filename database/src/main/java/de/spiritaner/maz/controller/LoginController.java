package de.spiritaner.maz.controller;

import de.spiritaner.maz.model.User;
import de.spiritaner.maz.util.Settings;
import de.spiritaner.maz.util.UpdateHelper;
import de.spiritaner.maz.util.database.CoreDatabase;
import de.spiritaner.maz.util.database.DatabaseFolder;
import de.spiritaner.maz.util.database.UserDatabase;
import de.spiritaner.maz.view.InitView;
import de.spiritaner.maz.view.MainView;
import de.spiritaner.maz.view.renderer.DatabaseFolderRenderer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.controlsfx.control.MaskerPane;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Florian Schwab
 * @version 2017.05.25
 */
public class LoginController implements Controller {

	final static Logger logger = Logger.getLogger(LoginController.class);

	@FXML
	private MaskerPane updateProgress;
	@FXML
	private Hyperlink updateLink;
	@FXML
	private ListView<DatabaseFolder> databaseListView;
	@FXML
	private Label versionLabel;
	@FXML
	private Label usernameLabel;
	@FXML
	private Label passwordLabel;
	@FXML
	private Label errorLabel;
	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Button loginButton;
	@FXML
	private Button initButton;
	@FXML
	private Button removeButton;
	@FXML
	private Button searchButton;
	@FXML
	private Button updateButton;
	@FXML
	private ImageView updateButtonImage;
	@FXML
	private VBox updateBox;
	@FXML
	private Label updateStatusLabel;
	//@FXML
	//private ProgressBar updateProgressBar;

	private Stage stage;
	private String releasePageLink = "";
	private String releaseJsonString = "";
	private BooleanProperty updateAvailable = new SimpleBooleanProperty(Boolean.FALSE);

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		final ResourceBundle guiText = ResourceBundle.getBundle("lang.gui");
		versionLabel.setText("Version " + guiText.getString("version"));

		usernameField.setPromptText(guiText.getString("username"));
		passwordField.setPromptText(guiText.getString("password"));
		usernameLabel.setText(guiText.getString("username") + ":");
		passwordLabel.setText(guiText.getString("password") + ":");

		loginButton.setText(guiText.getString("login"));
		initButton.setText(guiText.getString("init"));
		removeButton.setText(guiText.getString("remove"));
		searchButton.setText(guiText.getString("search"));
		updateButton.setText(guiText.getString("update"));

		setErrorMsg(null);

		updateBox.setVisible(false);
		updateBox.setManaged(false);

		databaseListView.setCellFactory(fileListView -> new DatabaseFolderRenderer());
		databaseListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		databaseListView.getItems().addListener((ListChangeListener<File>) change -> {
			initButton.setDisable(new File(Settings.get("database.parent", "./"), "./dbfiles").exists());
			checkUpdatePreconditions();
			checkRemovePreconditions();
		});
		databaseListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
			checkUpdatePreconditions();
			checkRemovePreconditions();
			checkLoginPreconditions();
		});

		// Disable/enable login button
		usernameField.textProperty().addListener((observableValue, oldValue, newValue) -> {
			checkLoginPreconditions();
			checkUpdatePreconditions();
		});
		passwordField.textProperty().addListener((observableValue, oldValue, newValue) -> {
			checkLoginPreconditions();
			checkUpdatePreconditions();
		});

		new Thread(this::searchForUpdate).start();
		searchDbFilesFolder();
	}

	private boolean checkRemovePreconditions() {
		final DatabaseFolder selectedDb = databaseListView.getSelectionModel().getSelectedItem();
		final boolean enable = selectedDb != null &&
				  !selectedDb.isMainDir() &&
				  !selectedDb.isLocked();

		removeButton.setDisable(!enable);
		return enable;
	}

	private boolean checkUpdatePreconditions() {
		final DatabaseFolder selectedDb = databaseListView.getSelectionModel().getSelectedItem();
		final boolean enable = selectedDb != null &&
				  (updateAvailable.get() || selectedDb.isOutdated()) &&
				  selectedDb.isMainDir() &&
				  !selectedDb.isLocked() &&
				  !usernameField.getText().trim().isEmpty() &&
				  !passwordField.getText().trim().isEmpty();

		updateButton.setDisable(!enable);
		return enable;
	}

	private boolean checkLoginPreconditions() {
		final DatabaseFolder selectedDb = databaseListView.getSelectionModel().getSelectedItem();
		final boolean enable = selectedDb != null &&
				  !usernameField.getText().trim().isEmpty() &&
				  !passwordField.getText().trim().isEmpty() &&
				  !selectedDb.isOutdated();

		loginButton.setDisable(!enable);
		return enable;
	}

	private void searchForUpdate() {
		  /*final RotateTransition rt = new RotateTransition(Duration.millis(2000), updateButtonImage);
		  Platform.runLater(() -> {
            rt.setByAngle(-360);
            rt.setCycleCount(Animation.INDEFINITE);
            rt.setAutoReverse(false);
            rt.setAxis(new Point3D(0,1,0));
            rt.setDelay(Duration.ZERO);
            rt.play();
        });*/
		logger.info("Start searching for updates");

		try {
			final String currentVersion = ResourceBundle.getBundle("lang.gui").getString("version");
			final URL url = new URL("https://api.github.com/repos/florian-boehm/maz-db/releases/latest");
			final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.addRequestProperty("http.agent", "database-updater-app");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code " + conn.getResponseCode());
			}

			final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String part;

			while ((part = reader.readLine()) != null) {
				releaseJsonString += part;
			}

			conn.disconnect();

			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(releaseJsonString);
			final String tagName = (String) jsonObject.get("tag_name");
			releasePageLink = (String) jsonObject.get("html_url");

			Platform.runLater(() -> {
				final Double newVersion = Double.parseDouble(tagName);
				final Double oldVersion = Double.parseDouble(currentVersion);

				if (newVersion > oldVersion) {
					logger.info("Found new release " + tagName + " on github");
					updateBox.setManaged(true);
					updateBox.setVisible(true);

					updateAvailable.set(Boolean.TRUE);
					checkUpdatePreconditions();
				}
				//stage.sizeToScene();
			});
		} catch (IOException | ParseException e) {
			logger.error(e);
		}

		//Platform.runLater(() -> rt.stop());
	}

	@Override
	public void onReopen() {

	}

	public void searchDbFilesFolder() {
		final String version = ResourceBundle.getBundle("lang.gui").getString("version");
		final File workingDirectory = new File(Settings.get("database.parent", "./"));
		File[] dbDirs = workingDirectory.listFiles((current, name) -> new File(current, name).isDirectory() && name.startsWith("dbfiles") /*&& new File(current, name + "/" + version + ".version").exists()*/);

		databaseListView.getItems().clear();

		if (dbDirs != null && dbDirs.length == 0)
			databaseListView.getItems().add(null);
		else
			for (File f : dbDirs) databaseListView.getItems().add(new DatabaseFolder(f));

		databaseListView.getSelectionModel().clearAndSelect(0);
	}

	public void selectDbParentDirectory(ActionEvent actionEvent) {
		final DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File(Settings.get("database.parent", "./")));

		final File selectedDirectory = directoryChooser.showDialog(new Stage());

		if (selectedDirectory != null && selectedDirectory.exists()) {
			Settings.set("database.parent", selectedDirectory.getAbsolutePath());
			searchDbFilesFolder();
		}
	}

	public void initDatabase(final ActionEvent actionEvent) {
		Stage secondStage = new Stage();
		secondStage.initOwner(stage);
		InitView.populateStage(secondStage);
		InitView.getController().setLoginController(this);
		secondStage.show();
	}

	public void tryLogin(final ActionEvent actionEvent) {
		setUpdateProgress("Anmeldung läuft ...", -1);
		Platform.runLater(() -> updateProgress.setVisible(true));

		final boolean loginSuccess = UserDatabase.validateLogin(new User(usernameField.getText(), passwordField.getText()), false);

		if(loginSuccess) {
			final File lockFile = new File(Settings.get("database.path", "./dbfiles/") + CoreDatabase.LOCK_FILE);
			final ButtonType readonly = new ButtonType("Lesezugriff", ButtonBar.ButtonData.FINISH);
			final ButtonType force = new ButtonType("Erzwingen", ButtonBar.ButtonData.APPLY);
			Optional<ButtonType> result = Optional.empty();

			if (lockFile.exists()) {
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setTitle("Datenbankzugriff");
				alert.setHeaderText("Datenbank möglicherweise in Verwendung ...");
				alert.setContentText("Mit \"Lesezugriff\" können Sie die Datenbank trotzdem öffnen, allerdings " +
						  "gehen jegliche Änderungen beim Schließen der Anwendung verloren. Mit \"Erzwingen\" können " +
						  "Sie den Schreibzugriff auf die Datenbank trotzdem versuchen.");
				alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				alert.getButtonTypes().clear();
				alert.getButtonTypes().add(readonly);
				alert.getButtonTypes().add(force);
				alert.getButtonTypes().add(ButtonType.CANCEL);

				result = alert.showAndWait();
			}

			if (!lockFile.exists() || (result.isPresent() && result.get() != ButtonType.CANCEL)) {
				if (result.isPresent() && result.get().equals(force)) {
					lockFile.delete();
					logger.info("IN HERE DELETE THE LOCK FILE");
				}

				new Thread(() -> {
					setUpdateProgress("Anmeldung läuft ...", -1);
					UserDatabase.validateLogin(new User(usernameField.getText(), passwordField.getText()), true);
					Platform.runLater(() -> MainView.populateStage(stage));
				}).start();
			}

			if (result.isPresent() && result.get() == ButtonType.CANCEL) {
				setUpdateProgress(null, -1);
			}
		} else {
			setUpdateProgress(null, -1);
			setErrorMsg(ResourceBundle.getBundle("lang.gui").getString("login_failed"));
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

	public void openReleasePage(final ActionEvent actionEvent) {
		if (releasePageLink != null) {
			new Thread(() -> {
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
				if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
					try {
						desktop.browse(new URI(releasePageLink));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	public void startUpdate(final ActionEvent actionEvent) {
		if (checkUpdatePreconditions()) {
			new Thread(() -> {
				final DatabaseFolder selectedDb = databaseListView.getSelectionModel().getSelectedItem();
				Settings.set("database.path", selectedDb.getAbsolutePath());

				updateProgress.setVisible(true);
				setUpdateProgress("Anmeldung läuft ...", -1);

				final User user = new User(usernameField.getText(), passwordField.getText());
				final boolean loginSuccess = UserDatabase.validateLogin(user, false);
				final UpdateHelper updateHelper = new UpdateHelper(this, releaseJsonString, user);

				if (loginSuccess) {
					if(selectedDb.isOutdated())
						updateHelper.startDbMigration();
					else
						updateHelper.startFullUpdate();
				} else {
					setUpdateProgress(null, -1);
					setErrorMsg(ResourceBundle.getBundle("lang.gui").getString("login_failed"));
				}
			}).start();
		}
	}

	public void removeDbBackup(final ActionEvent actionEvent) {
		if (checkRemovePreconditions()) {
			final File selectedDb = databaseListView.getSelectionModel().getSelectedItem();

			if (selectedDb != null) {
				try {
					FileUtils.deleteDirectory(selectedDb);
					searchDbFilesFolder();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
	}

	public Stage getStage() {
		return stage;
	}

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setUpdateProgress(final String msg, final double progress) {
		Platform.runLater(() -> {
			if (msg != null) {
				updateProgress.setText(msg);
				updateProgress.setProgress(progress);
			} else {
				updateProgress.setVisible(false);
				updateProgress.setProgress(-1);
			}
		});
	}
}

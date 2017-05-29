package de.spiritaner.maz.controller;

import de.spiritaner.maz.model.User;
import de.spiritaner.maz.util.database.DataDatabase;
import de.spiritaner.maz.view.dialog.ExceptionDialog;
import de.spiritaner.maz.view.dialog.LoginDialog;
import de.spiritaner.maz.util.Settings;
import de.spiritaner.maz.util.database.UserDatabase;
import de.spiritaner.maz.util.validator.TextValidator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import liquibase.exception.LiquibaseException;
import org.apache.log4j.Logger;
import org.controlsfx.control.MaskerPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * @author Florian Schwab
 * @version 2017.05.28
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
    private MaskerPane maskerPane;

    @FXML
    private Button setupButton;

    private TextValidator usernameFieldValidator;
    private TextValidator passwordFieldValidator;
    private TextValidator passwordFieldCheckValidator;

    private Stage stage;

    private LoginController loginController;

    public void initialize(URL location, ResourceBundle resources) {
        usernameFieldValidator = TextValidator.create(usernameField).fieldName("Benutzername").min(3).max(16).notEmpty(true).removeAll(" ").justText().validateOnChange();
        passwordFieldValidator = TextValidator.create(passwordField).fieldName("Passwort").min(8).notEmpty(true).removeAll(" ").validateOnChange();
        passwordFieldCheckValidator = TextValidator.create(passwordCheckField).fieldName("Wiederholtes Passwort").equals(passwordField).validateOnChange();
        maskerPane.setVisible(false);
    }

    public void setupDatabase(ActionEvent actionEvent) {
        boolean usernameValid = usernameFieldValidator.validate();
        boolean passwordValid = passwordFieldValidator.validate();
        boolean passwordCheckValid = passwordFieldCheckValidator.validate();

        if (usernameValid && passwordValid && passwordCheckValid) {
            new Thread(() -> {
                try {
                    Platform.runLater(() -> {
                        maskerPane.setProgress(-1);
                        maskerPane.setText("Lege Benutzerdatenbank an ...");
                        maskerPane.setVisible(true);
                    });
                    User user = new User();
                    user.setPassword(passwordField.getText());
                    user.setUsername(usernameField.getText());

                    UserDatabase.init(user);
                    logger.info(user.getUsername());
                    logger.info(user.getPassword());
                    logger.info(user.getUnencryptedDatabaseKey());

                    Platform.runLater(() -> maskerPane.setText("Lege Stammdatenbank an ..."));
                    DataDatabase.init(user);

                    if (UserDatabase.isPopulated()/* && stage != null*/) {
                        // Create the version file in the new created database directory
                        new File(Settings.get("database.path", "./dbfiles/") + ResourceBundle.getBundle("lang.gui").getString("version") + ".version").createNewFile();

                        Platform.runLater(() -> {
                            maskerPane.setText("Initialisierung erfolgreich!");
                            maskerPane.setProgress(1);
                        });

                        Thread.sleep(3000);

                        Platform.runLater(() -> {
                            loginController.searchDbFilesFolder();
                            stage.close();
                        });
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        maskerPane.setText("Initialisierung fehlerhaft!");
                        maskerPane.setProgress(1);
                    });

                    logger.error(e);
                    ExceptionDialog.show(e);
                }
            }).start();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public void cancelInit(ActionEvent actionEvent) {
        stage.close();
    }
}

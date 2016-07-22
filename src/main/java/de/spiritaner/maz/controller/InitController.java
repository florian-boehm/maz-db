package de.spiritaner.maz.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
public class InitController implements Initializable {
	@FXML
	private PasswordField passwordField;

	@FXML
	private TextField usernameField;

	@FXML
	private TextField setupButton;

	public void initialize(URL location, ResourceBundle resources) {

	}
}

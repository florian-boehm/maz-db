package de.spiritaner.maz.controller;

import de.spiritaner.maz.dialog.ProgressDialog;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class UpdaterController implements Initializable {

	final static Logger logger = Logger.getLogger(UpdaterController.class);

	@FXML
	private Label currentVersionLabel;
	@FXML
	private Label latestVersionLabel;
	@FXML
	private Button updateDatabaseButton;
	@FXML
	private ProgressIndicator progressIndicator;
	@FXML
	private TextArea changesTextArea;

	private final String baseURL = "https://api.github.com/repos/fschwab/maz-db/";
	private String currentVersion = "";
	private String releaseJsonString = "";
	private Stage stage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Locale.setDefault(new Locale("de","DE"));
		final ResourceBundle guiText = ResourceBundle.getBundle("lang.gui");
		currentVersion = guiText.getString("version");

		currentVersionLabel.setText(currentVersion);
		progressIndicator.setVisible(true);

		new Thread(this::fetchLatestRelease).start();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void updateDatabase(ActionEvent actionEvent) {
		ProgressDialog.startUpdate(stage, releaseJsonString);
	}

	public void fetchLatestRelease() {
		try {
			URL url = new URL(baseURL+"releases/latest");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.addRequestProperty("http.agent", "database-updater-app");

			if(conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code " + conn.getResponseCode());
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			releaseJsonString = "";
			String part;

			while((part = reader.readLine()) != null) {
				releaseJsonString += part;
			}

			conn.disconnect();

			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(releaseJsonString);
			final String tagName = (String) jsonObject.get("tag_name");

			Platform.runLater(() -> {
				if(!tagName.equals(currentVersion)) {
					latestVersionLabel.setText("(Update verfügbar: " + tagName + ")");
					latestVersionLabel.setVisible(true);
					updateDatabaseButton.setVisible(true);

					//Font defaultFont = Font.getDefault();

					/*for(String line : ((String) jsonObject.get("body")).split("\\n")) {
						Text richText = new Text(line);

						//System.out.println("#"+line);

						//if(line.startsWith("*") && line.endsWith("*"))
						//	richText.setFont(Font.font(defaultFont.getFamily(), FontPosture.ITALIC, defaultFont.getSize()));

						changesTextFlow.getChildren().add(richText);
					}*/
					changesTextArea.setText((String) jsonObject.get("body"));
				} else {
					latestVersionLabel.setText("(aktuell)");
					latestVersionLabel.setVisible(true);
					changesTextArea.setVisible(false);
					changesTextArea.setManaged(false);
				}

				stage.sizeToScene();
			});
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		progressIndicator.setVisible(false);
	}

	public void closeUpdater(ActionEvent actionEvent) {
		Platform.runLater(() -> stage.close());
	}
}

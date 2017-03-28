package de.spiritaner.maz.controller;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

public interface Controller extends Initializable {

    void setStage(Stage stage);

    void onReopen();
}

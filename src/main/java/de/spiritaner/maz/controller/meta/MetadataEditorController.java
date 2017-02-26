package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.controller.Controller;
import de.spiritaner.maz.model.meta.MetaClass;
import de.spiritaner.maz.util.DataDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class MetadataEditorController extends BorderPane implements Initializable, Controller {

    final static Logger logger = Logger.getLogger(MetadataEditorController.class);

    @FXML protected TableView<? extends MetaClass> metaClassTable;
    @FXML private TableColumn<? extends MetaClass, Long> metaClassIdColumn;
    @FXML private TableColumn<? extends MetaClass, String> metaClassDescriptionColumn;

    private Stage stage;
    protected EntityManager em = DataDatabase.getFactory().createEntityManager();

    public MetadataEditorController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/metadata_editor.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            System.out.println();
        }
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        metaClassIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        metaClassDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        load();
    }

    public abstract void load();

    public abstract void create(ActionEvent actionEvent);

    public abstract void edit(ActionEvent actionEvent);

    public abstract void remove(ActionEvent actionEvent);
}

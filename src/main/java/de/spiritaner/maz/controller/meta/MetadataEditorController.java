package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.dialog.MetadataEditorDialog;
import de.spiritaner.maz.dialog.MetadataRemoveDialog;
import de.spiritaner.maz.model.meta.MetaClass;
import de.spiritaner.maz.util.DataDatabase;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public abstract class MetadataEditorController<T extends MetaClass> extends BorderPane implements Initializable {

    final static Logger logger = Logger.getLogger(MetadataEditorController.class);

    @FXML protected TableView<T> metaClassTable;
    @FXML private TableColumn<T, Long> metaClassIdColumn;
    @FXML private TableColumn<T, String> metaClassDescriptionColumn;

    protected EntityManager em = DataDatabase.getFactory().createEntityManager();
    private Class<T> cls;

    public MetadataEditorController(Class<T> cls) {
        this.cls = cls;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/meta/metadata_editor.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            System.out.println();
        }
    }

    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        metaClassIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        metaClassDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        load();
    }

    public void load() {
        metaClassTable.setItems(FXCollections.observableArrayList(em.createNamedQuery(cls.getSimpleName()+".findAll",cls).getResultList()));
    }

    public void create(final ActionEvent actionEvent) {
        final Optional<String> result = MetadataEditorDialog.showAndWait(null, getMetaName());

        result.ifPresent((value) -> {
            try {
                T newMetaClass = cls.newInstance();
                newMetaClass.setDescription(value);
                em.getTransaction().begin();
                em.persist(newMetaClass);
                em.getTransaction().commit();

                load();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public void edit(final ActionEvent actionEvent) {
        final MetaClass metaClassObj = metaClassTable.getSelectionModel().getSelectedItem();
        final Optional<String> result = MetadataEditorDialog.showAndWait(metaClassObj, getMetaName());

        result.ifPresent((value) -> {
            em.getTransaction().begin();
            T existingGender = em.find(cls, metaClassObj.getId());
            existingGender.setDescription(value);
            em.getTransaction().commit();

            load();
        });
    }

    public void remove(final ActionEvent actionEvent) {
        final MetaClass metaClassObj = metaClassTable.getSelectionModel().getSelectedItem();
        final Optional<ButtonType> result = MetadataRemoveDialog.showAndWait(metaClassObj, getMetaName());

        if (result.get() == ButtonType.OK){
            try {
                em.getTransaction().begin();
                T obsoleteGender = em.find(cls, metaClassObj.getId());
                em.remove(obsoleteGender);
                em.getTransaction().commit();

                load();
            } catch(RollbackException e) {
                MetadataRemoveDialog.showFailureAndWait(metaClassObj, getMetaName(), e);
            }
        }
    }

    public abstract String getMetaName();
}

package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.dialog.MetadataEditorDialog;
import de.spiritaner.maz.model.meta.Gender;
import de.spiritaner.maz.model.meta.MetaClass;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;

import java.util.Optional;

/**
 * Created by florian on 2/26/17.
 */
public class GenderEditorController extends MetadataEditorController {

    private static final String META_NAME = "Geschlecht";

    @Override
    public void load() {
        super.metaClassTable.setItems(FXCollections.observableArrayList(em.createNamedQuery("Gender.findAll").getResultList()));
    }

    @Override
    public void create(ActionEvent actionEvent) {
        Optional<String> result = MetadataEditorDialog.showAndWait(null, META_NAME);

        result.ifPresent((value) -> {
            Gender newGender = new Gender();
            newGender.setDescription(value);
            em.getTransaction().begin();
            em.persist(newGender);
            em.getTransaction().commit();
        });

        load();
    }

    @Override
    public void edit(ActionEvent actionEvent) {
        MetaClass metaClassObj = super.metaClassTable.getSelectionModel().getSelectedItem();
        Optional<String> result = MetadataEditorDialog.showAndWait(metaClassObj, META_NAME);

        result.ifPresent((value) -> {
            em.getTransaction().begin();
            Gender existingGender = em.find(Gender.class, metaClassObj.getId());
            existingGender.setDescription(value);
            em.getTransaction().commit();
        });

        load();
    }

    @Override
    public void remove(ActionEvent actionEvent) {
        load();
    }
}

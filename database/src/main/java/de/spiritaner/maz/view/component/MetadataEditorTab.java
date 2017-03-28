package de.spiritaner.maz.view.component;

import de.spiritaner.maz.controller.meta.MetadataEditorController;
import javafx.scene.control.Tab;

public class MetadataEditorTab extends Tab {

    private MetadataEditorController metadataEditorController;

    public MetadataEditorTab(MetadataEditorController metadataEditorController) {
        setClosable(false);
        this.metadataEditorController = metadataEditorController;
        this.setContent(metadataEditorController);
    }

    public static MetadataEditorTab valueOf(String metaClass) {
        Class<? extends MetadataEditorController> cls = null;
        try {
            cls = (Class<? extends MetadataEditorController>) Class.forName(metaClass);
            return new MetadataEditorTab(cls.newInstance());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }
}

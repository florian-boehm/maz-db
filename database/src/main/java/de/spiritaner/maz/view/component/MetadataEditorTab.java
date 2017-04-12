package de.spiritaner.maz.view.component;

import de.spiritaner.maz.controller.meta.MetadataOverviewController;
import javafx.scene.control.Tab;

public class MetadataEditorTab extends Tab {

    private MetadataOverviewController metadataOverviewController;

    public MetadataEditorTab(MetadataOverviewController metadataOverviewController) {
        setClosable(false);
        this.metadataOverviewController = metadataOverviewController;
        this.setContent(metadataOverviewController);
    }

    public static MetadataEditorTab valueOf(String metaClass) {
        Class<? extends MetadataOverviewController> cls = null;
        try {
            cls = (Class<? extends MetadataOverviewController>) Class.forName(metaClass);
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

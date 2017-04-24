package de.spiritaner.maz.view.component;

import de.spiritaner.maz.controller.meta.MetadataOverviewController;
import javafx.scene.control.Tab;

@SuppressWarnings("unchecked")
public class MetadataEditorTab extends Tab {

	//private MetadataOverviewController metadataOverviewController;

	/*public*/ MetadataEditorTab(MetadataOverviewController metadataOverviewController) {
		setClosable(false);
		//this.metadataOverviewController = metadataOverviewController;
		this.setContent(metadataOverviewController);
	}

	public static MetadataEditorTab valueOf(String metaClass) {
		Class<? extends MetadataOverviewController> cls = null;
		try {
			cls = (Class<? extends MetadataOverviewController>) Class.forName(metaClass);
			return new MetadataEditorTab(cls.newInstance());
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}

		return null;
	}
}

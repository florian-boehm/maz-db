package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.Diocese;

/**
 * Created by florian on 2/26/17.
 */
public class DioceseOverviewController extends MetadataOverviewController<Diocese> {

    public DioceseOverviewController() {
        super(Diocese.class);
    }

    @Override
    public String getMetaName() {
        return guiText.getString("diocese");
    }
}

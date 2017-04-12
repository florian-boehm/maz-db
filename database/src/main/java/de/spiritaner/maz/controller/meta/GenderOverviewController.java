package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.Gender;

/**
 * Created by florian on 2/26/17.
 */
public class GenderOverviewController extends MetadataOverviewController<Gender> {

    public GenderOverviewController() {
        super(Gender.class);
    }

    @Override
    public String getMetaName() {
        return "Geschlecht";
    }
}

package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.PersonGroup;

public class PersonGroupOverviewController extends MetadataOverviewController<PersonGroup> {

    public PersonGroupOverviewController() {
        super(PersonGroup.class);
    }

    @Override
    // TODO Extract strings
    public String getMetaName() {
        return "Personengruppe";
    }
}

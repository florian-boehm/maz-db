package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.PersonGroup;

public class PersonGroupOverviewController extends MetadataOverviewController<PersonGroup> {

    public PersonGroupOverviewController() {
        super(PersonGroup.class);
    }

    @Override
    public String getMetaName() {
        return guiText.getString("person_group");
    }
}

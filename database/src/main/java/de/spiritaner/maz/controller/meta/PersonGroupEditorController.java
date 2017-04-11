package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.PersonGroup;

public class PersonGroupEditorController extends MetadataEditorController<PersonGroup> {

    public PersonGroupEditorController() {
        super(PersonGroup.class);
    }

    @Override
    public String getMetaName() {
        return "Personengruppe";
    }
}

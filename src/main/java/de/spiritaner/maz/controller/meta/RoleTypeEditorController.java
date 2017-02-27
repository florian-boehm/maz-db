package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.RoleType;

/**
 * Created by florian on 2/26/17.
 */
public class RoleTypeEditorController extends MetadataEditorController<RoleType> {

    public RoleTypeEditorController() {
        super(RoleType.class);
    }

    @Override
    public String getMetaName() {
        return "Funktion";
    }
}

package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.ResidenceType;

/**
 * Created by florian on 2/26/17.
 */
public class ResidenceTypeEditorController extends MetadataEditorController<ResidenceType> {

    public ResidenceTypeEditorController() {
        super(ResidenceType.class);
    }

    @Override
    public String getMetaName() {
        return "Wohnungsart";
    }
}

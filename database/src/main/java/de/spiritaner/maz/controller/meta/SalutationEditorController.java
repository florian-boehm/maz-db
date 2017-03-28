package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.Salutation;

/**
 * Created by florian on 2/26/17.
 */
public class SalutationEditorController extends MetadataEditorController<Salutation> {

    public SalutationEditorController() {
        super(Salutation.class);
    }

    @Override
    public String getMetaName() {
        return "Anrede";
    }
}

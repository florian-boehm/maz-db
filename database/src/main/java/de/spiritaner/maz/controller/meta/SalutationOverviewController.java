package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.Salutation;

/**
 * Created by florian on 2/26/17.
 */
public class SalutationOverviewController extends MetadataOverviewController<Salutation> {

    public SalutationOverviewController() {
        super(Salutation.class);
    }

    @Override
    public String getMetaName() {
        return "Anrede";
    }
}

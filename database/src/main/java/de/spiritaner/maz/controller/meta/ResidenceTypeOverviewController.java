package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.ResidenceType;

public class ResidenceTypeOverviewController extends MetadataOverviewController<ResidenceType> {

    public ResidenceTypeOverviewController() {
        super(ResidenceType.class);
    }

    @Override
    public String getMetaName() {
        return "Wohnungsart";
    }
}

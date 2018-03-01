package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.RelationshipType;

public class RelationshipTypeOverviewController extends MetadataOverviewController<RelationshipType> {

    public RelationshipTypeOverviewController() {
        super(RelationshipType.class);
    }

    @Override
    // TODO Extract strings
    public String getMetaName() {
        return "Beziehungsart";
    }
}
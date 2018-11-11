package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.RelationshipType;

public class RelationshipTypeOverviewController extends MetadataOverviewController<RelationshipType> {

    public RelationshipTypeOverviewController() {
        super(RelationshipType.class);
    }

    @Override
    public String getMetaName() {
        return guiText.getString("relationship_type");
    }
}
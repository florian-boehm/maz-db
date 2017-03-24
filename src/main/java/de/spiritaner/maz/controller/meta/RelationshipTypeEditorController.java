package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.RelationshipType;

public class RelationshipTypeEditorController extends MetadataEditorController<RelationshipType> {

    public RelationshipTypeEditorController() {
        super(RelationshipType.class);
    }

    @Override
    public String getMetaName() {
        return "Beziehungsart";
    }
}
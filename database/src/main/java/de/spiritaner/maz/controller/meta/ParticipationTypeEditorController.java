package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.ParticipationType;

public class ParticipationTypeEditorController extends MetadataEditorController<ParticipationType> {

    public ParticipationTypeEditorController() {
        super(ParticipationType.class);
    }

    @Override
    public String getMetaName() {
        return "Teilnehmerart";
    }
}

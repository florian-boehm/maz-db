package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.ParticipantType;

/**
 * Created by florian on 2/26/17.
 */
public class ParticipantTypeEditorController extends MetadataEditorController<ParticipantType> {

    public ParticipantTypeEditorController() {
        super(ParticipantType.class);
    }

    @Override
    public String getMetaName() {
        return "Teilnehmerart";
    }
}

package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.ParticipationType;

public class ParticipationTypeOverviewController extends MetadataOverviewController<ParticipationType> {

    public ParticipationTypeOverviewController() {
        super(ParticipationType.class);
    }

    @Override
    public String getMetaName() {
        return "Teilnehmerart";
    }
}

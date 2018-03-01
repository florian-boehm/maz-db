package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.EventType;

/**
 * Created by florian on 2/26/17.
 */
public class EventTypeOverviewController extends MetadataOverviewController<EventType> {

    public EventTypeOverviewController() {
        super(EventType.class);
    }

    @Override
    // TODO Extract strings
    public String getMetaName() {
        return "Veranstaltungsart";
    }
}

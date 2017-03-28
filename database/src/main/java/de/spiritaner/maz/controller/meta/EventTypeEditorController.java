package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.EventType;

/**
 * Created by florian on 2/26/17.
 */
public class EventTypeEditorController extends MetadataEditorController<EventType> {

    public EventTypeEditorController() {
        super(EventType.class);
    }

    @Override
    public String getMetaName() {
        return "Veranstaltungsart";
    }
}

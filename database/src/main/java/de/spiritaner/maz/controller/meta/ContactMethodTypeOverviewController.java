package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.ContactMethodType;

/**
 * Created by florian on 2/26/17.
 */
public class ContactMethodTypeOverviewController extends MetadataOverviewController<ContactMethodType> {

    public ContactMethodTypeOverviewController() {
        super(ContactMethodType.class);
    }

    @Override
    // TODO Extract strings
    public String getMetaName() {
        return "Kontaktart";
    }
}

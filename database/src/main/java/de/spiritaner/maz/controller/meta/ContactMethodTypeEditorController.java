package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.ContactMethodType;

/**
 * Created by florian on 2/26/17.
 */
public class ContactMethodTypeEditorController extends MetadataEditorController<ContactMethodType> {

    public ContactMethodTypeEditorController() {
        super(ContactMethodType.class);
    }

    @Override
    public String getMetaName() {
        return "Kontaktart";
    }
}

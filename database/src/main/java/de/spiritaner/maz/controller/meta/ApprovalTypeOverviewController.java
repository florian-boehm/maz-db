package de.spiritaner.maz.controller.meta;

import de.spiritaner.maz.model.meta.ApprovalType;

/**
 * Created by florian on 2/26/17.
 */
public class ApprovalTypeOverviewController extends MetadataOverviewController<ApprovalType> {

    public ApprovalTypeOverviewController() {
        super(ApprovalType.class);
    }

    @Override
    public String getMetaName() {
        return "Einwilligung";
    }
}

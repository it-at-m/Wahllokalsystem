package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisartModel;

public enum EreignisartDTO {
    VORFALL, VORKOMMNIS;

    public static EreignisartDTO fromModel(EreignisartModel model) {
        return switch (model) {
        case VORFALL -> VORFALL;
        case VORKOMMNIS -> VORKOMMNIS;
        };
    }
}

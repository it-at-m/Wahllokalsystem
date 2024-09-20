package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.EreignisartDTO;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisartModel;

public enum EreignisartEntity {
    VORFALL, VORKOMMNIS;

    public static EreignisartEntity fromModel(EreignisartModel model) {
        return switch (model) {
        case VORFALL -> VORFALL;
        case VORKOMMNIS -> VORKOMMNIS;
        };
    }

    public static EreignisartEntity fromDto(EreignisartDTO entity) {
        return switch (entity) {
        case VORFALL -> VORFALL;
        case VORKOMMNIS -> VORKOMMNIS;
        };
    }
}

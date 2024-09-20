package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisartEntity;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.EreignisartDTO;

public enum EreignisartModel {
    VORFALL, VORKOMMNIS;

    public static EreignisartModel fromEntity(EreignisartEntity entity) {
        return switch (entity) {
        case VORFALL -> VORFALL;
        case VORKOMMNIS -> VORKOMMNIS;
        };
    }

    public static EreignisartModel fromDto(EreignisartDTO entity) {
        return switch (entity) {
        case VORFALL -> VORFALL;
        case VORKOMMNIS -> VORKOMMNIS;
        };
    }
}

package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignis;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.EreignisDTO;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModel;

public class TestdataFactory {

    // Entities
    public static Ereignis createEreignisEntityWithNoData() {
        return new Ereignis(null, null, null, null);
    }

    public static Ereignis createEreignisEntityWithData(String wahlbezirkID, String beschreibung, java.time.LocalDateTime uhrzeit, Ereignisart ereignisart) {
        return new Ereignis(wahlbezirkID, beschreibung, uhrzeit, ereignisart);
    }

    public static Ereignis createEreignisEntityFromModel(EreignisModel ereignisModel) {
        return new Ereignis(ereignisModel.wahlbezirkID(), ereignisModel.beschreibung(), ereignisModel.uhrzeit(), ereignisModel.ereignisart());
    }

    // Models
    public static EreignisModel createEreignisModelWithNoData() {
        return new EreignisModel(null, null, null, null);
    }

    public static EreignisModel createEreignisModelWithData(String wahlbezirkID, String beschreibung, java.time.LocalDateTime uhrzeit, Ereignisart ereignisart) {
        return new EreignisModel(wahlbezirkID, beschreibung, uhrzeit, ereignisart);
    }

    public static EreignisModel createEreignisModelFromEntity(Ereignis ereignisEntity) {
        return new EreignisModel(ereignisEntity.getWahlbezirkID(), ereignisEntity.getBeschreibung(), ereignisEntity.getUhrzeit(), ereignisEntity.getEreignisart());
    }

    public static EreignisModel createEreignisModelFromDTO(EreignisDTO ereignisDTO) {
        return new EreignisModel(ereignisDTO.wahlbezirkID(), ereignisDTO.beschreibung(), ereignisDTO.uhrzeit(), ereignisDTO.ereignisart());
    }

    // DTOs
    public static EreignisDTO createEreignisDTOWithNoData() {
        return new EreignisDTO(null, null, null, null);
    }

    public static EreignisDTO createEreignisDTOWithData(String wahlbezirkID, String beschreibung, java.time.LocalDateTime uhrzeit, Ereignisart ereignisart) {
        return new EreignisDTO(wahlbezirkID, beschreibung, uhrzeit, ereignisart);
    }

    public static EreignisDTO createEreignisDTOFromModel(EreignisModel ereignisModel) {
        return new EreignisDTO(ereignisModel.wahlbezirkID(), ereignisModel.beschreibung(), ereignisModel.uhrzeit(), ereignisModel.ereignisart());
    }
}

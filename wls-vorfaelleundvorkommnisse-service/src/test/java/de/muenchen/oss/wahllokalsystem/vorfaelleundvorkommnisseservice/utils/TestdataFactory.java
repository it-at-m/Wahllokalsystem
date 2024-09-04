package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignis;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModel;

public class TestdataFactory {

    public static Ereignis createEreignisEntityWithNoData() {
        return new Ereignis();
    }

    public static Ereignis createEreignisEntityWithData(String wahlbezirkID, String beschreibung, java.time.LocalDateTime uhrzeit, Ereignisart ereignisart) {
        return new Ereignis(wahlbezirkID, beschreibung, uhrzeit, ereignisart);
    }

    public static Ereignis createEreignisEntityFromModel(EreignisModel ereignisModel) {
        return new Ereignis(ereignisModel.wahlbezirkID(), ereignisModel.beschreibung(), ereignisModel.uhrzeit(), ereignisModel.ereignisart());
    }

    public static EreignisModel createEreignisModelWithNoData() {
        return new EreignisModel(null, null, null, null);
    }

    public static EreignisModel createEreignisModelWithData(String wahlbezirkID, String beschreibung, java.time.LocalDateTime uhrzeit, Ereignisart ereignisart) {
        return new EreignisModel(wahlbezirkID, beschreibung, uhrzeit, ereignisart);
    }

    public static EreignisModel createEreignisModelFromEntity(Ereignis ereignisEntity) {
        return new EreignisModel(ereignisEntity.getWahlbezirkID(), ereignisEntity.getBeschreibung(), ereignisEntity.getUhrzeit(), ereignisEntity.getEreignisart());
    }
}

package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereigniseintrag;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisse;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModel;

public class TestdataFactory {

    public static Ereignisse createEreignisEntityWithNoData() {
        return new Ereignisse();
    }

    public static Ereignisse createEreignisEntityWithData(String wahlbezirkID, Boolean keineVorfaelle, Boolean keineVorkommnisse, java.util.List<Ereigniseintrag> ereigniseintrag) {
        return new Ereignisse(wahlbezirkID, keineVorfaelle, keineVorkommnisse, ereigniseintrag);
    }

    public static Ereignisse createEreignisEntityFromModel(EreignisModel ereignisModel) {
        return new Ereignisse(ereignisModel.wahlbezirkID(), ereignisModel.keineVorfaelle(), ereignisModel.keineVorkommnisse(), ereignisModel.ereigniseintrag());
    }

    public static EreignisModel createEreignisModelWithNoData() {
        return new EreignisModel(null, null, null, null);
    }

    public static EreignisModel createEreignisModelWithData(String wahlbezirkID, Boolean keineVorfaelle, Boolean keineVorkommnisse, java.util.List<Ereigniseintrag> ereigniseintrag) {
        return new EreignisModel(wahlbezirkID, keineVorfaelle, keineVorkommnisse, ereigniseintrag);
    }

    public static EreignisModel createEreignisModelFromEntity(Ereignisse ereignisEntity) {
        return new EreignisModel(ereignisEntity.getWahlbezirkID(), ereignisEntity.isKeineVorfaelle(), ereignisEntity.isKeineVorkommnisse(), ereignisEntity.getEreigniseintrag());
    }
}

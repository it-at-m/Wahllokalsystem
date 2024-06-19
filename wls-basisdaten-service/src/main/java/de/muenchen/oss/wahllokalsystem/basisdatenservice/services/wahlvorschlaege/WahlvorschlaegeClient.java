package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.api.WahlvorschlagControllerApi;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.model.WahlvorschlaegeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahlvorschlaegeClient {

    private final WahlvorschlagControllerApi wahlvorschlagControllerApi;

    public WahlvorschlaegeDTO getWahlvorschlaege(String wahlID, String wahlbezirkID) {
        return wahlvorschlagControllerApi.loadWahlvorschlaege(wahlID, wahlbezirkID);
    }

}

package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.client.WahlvorschlagControllerApi;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DemoClient {

    private final WahlvorschlagControllerApi wahlvorschlagControllerApi;

    public DemoDTO getDemo() {
        val wahlvorschlaege = wahlvorschlagControllerApi.loadWahlvorschlaege("wahlID", "wahlbezirkID");

        return new DemoDTO(wahlvorschlaege.getStimmzettelgebietID(), "" + wahlvorschlaege.getWahlvorschlaege().size());
    }

}

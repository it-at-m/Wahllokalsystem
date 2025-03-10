package de.muenchen.oss.wahllokalsystem.authservice.client;

import de.muenchen.oss.wahllokalsystem.authservice.eai.infomanagement.client.KonfigurierterWahltagControllerApi;
import de.muenchen.oss.wahllokalsystem.authservice.service.WahltagClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BasisdatenServiceClient implements WahltagClient {

    private final KonfigurierterWahltagControllerApi konfigurierterWahltagControllerApi;

    @Override
    public boolean isWahltagActive(final String wahltagID) {
        return konfigurierterWahltagControllerApi.isWahltagActive(wahltagID);
    }
}

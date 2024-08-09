package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.infomanagement.client.KonfigurierterWahltagControllerApi;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KonfigurierterWahltagClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
@RequiredArgsConstructor
@Slf4j
public class KonfigurierterWahltagClientImpl implements KonfigurierterWahltagClient {

    private final ExceptionFactory exceptionFactory;
    private final KonfigurierterWahltagControllerApi konfigurierterWahltagControllerApi;
    private final KonfigurierterWahltagClientMapper konfigurierterWahltagClientMapper;

    @Override
    public KonfigurierterWahltagModel getKonfigurierterWahltag() throws WlsException {

        final KonfigurierterWahltagDTO konfigurierterWahltagDTO;
        try {
            konfigurierterWahltagDTO = konfigurierterWahltagControllerApi.getKonfigurierterWahltag();
        } catch (final Exception exception) {
            log.info("exception on getKonfigurierterWahltag from external", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_SERVICE);
        }
        if (konfigurierterWahltagDTO == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETKOPFDATEN_NO_KONFIGURIERTERWAHLTAG);
        }
        return konfigurierterWahltagClientMapper.fromRemoteClientDTOToModel(konfigurierterWahltagDTO);
    }
}

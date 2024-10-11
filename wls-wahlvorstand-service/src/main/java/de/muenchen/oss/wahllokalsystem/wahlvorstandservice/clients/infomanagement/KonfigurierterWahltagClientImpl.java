package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.infomanagement;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.infomanagement.client.KonfigurierterWahltagControllerApi;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.KonfigurierterWahltagClient;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
public class KonfigurierterWahltagClientImpl implements KonfigurierterWahltagClient {

    private final ExceptionFactory exceptionFactory;
    private final KonfigurierterWahltagControllerApi konfigurierterWahltagControllerApi;
    private final KonfigurierterWahltagClientMapper konfigurierterWahltagClientMapper;

    @Override
    public KonfigurierterWahltagModel getKonfigurierterWahltag() throws WlsException {
        final KonfigurierterWahltagDTO konfigurierterWahltagDTO;
        try {
            konfigurierterWahltagDTO = konfigurierterWahltagControllerApi.getKonfigurierterWahltag();
        } catch (Exception exception) {
            log.info("exception on getKonfigurierterWahltag from external", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT);
        }

        if (konfigurierterWahltagDTO == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.INFOMANAGEMENT_WAHLTAG_NULL_OR_EMPTY);
        }
        return konfigurierterWahltagClientMapper.fromRemoteClientDTOToModel(konfigurierterWahltagDTO);
    }
}

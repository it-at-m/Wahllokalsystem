package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.informanagement;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.infomanagement.client.KonfigurierterWahltagControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.CurrentWahltagClient;
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
public class InfomanagementClientImpl implements CurrentWahltagClient {

    private final KonfigurierterWahltagControllerApi konfigurierterWahltagControllerApi;

    private final ExceptionFactory exceptionFactory;

    @Override
    public String getWahltagID() {
        final KonfigurierterWahltagDTO konfigurierterWahltag;
        try {
            konfigurierterWahltag = konfigurierterWahltagControllerApi.getKonfigurierterWahltag();
        } catch (final WlsException wlsException) {
            log.debug("found WlsException", wlsException);
            throw wlsException;
        } catch (final Exception e) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT);
        }

        if (konfigurierterWahltag == null || konfigurierterWahltag.getWahltagID() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.INFOMANAGEMENT_WAHLTAG_NULL_OR_EMPTY);
        }

        return konfigurierterWahltag.getWahltagID();
    }
}

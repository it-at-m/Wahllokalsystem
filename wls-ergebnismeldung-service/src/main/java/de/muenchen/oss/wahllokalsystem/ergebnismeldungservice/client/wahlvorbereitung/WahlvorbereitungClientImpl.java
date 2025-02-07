package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.wahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.wahlvorbereitung.client.UrnenwahlSchliessungsUhrzeitControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.wahlvorbereitung.model.UrnenwahlSchliessungsUhrzeitDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.UrnenwahlClient;
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
public class WahlvorbereitungClientImpl implements UrnenwahlClient {

    private final UrnenwahlSchliessungsUhrzeitControllerApi urnenwahlSchliessungsUhrzeitControllerApi;

    private final ExceptionFactory exceptionFactory;

    @Override
    public boolean isWahlbezirkGeschlossen(final String wahlbezirkID) {
        assertWahlbezirkWithSchliessungsuhrzeitExists(wahlbezirkID);
        return true;
    }

    private void assertWahlbezirkWithSchliessungsuhrzeitExists(final String wahlbezirkID) {
        final UrnenwahlSchliessungsUhrzeitDTO urnenwahlSchliessungsUhrzeitDTO;
        try {
            urnenwahlSchliessungsUhrzeitDTO = urnenwahlSchliessungsUhrzeitControllerApi.getUrnenwahlSchliessungsUhrzeit(wahlbezirkID);
        } catch (final WlsException wlsException) {
            log.debug("found WlsException", wlsException);
            throw wlsException;
        } catch (final Exception e) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_WAHLVORBEREITUNG);
        }

        if (urnenwahlSchliessungsUhrzeitDTO == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.WAHLVORBEREITUNG_SCHLIESSUNGSUHRZEIT_NULL_OR_EMPTY);
        }
    }
}

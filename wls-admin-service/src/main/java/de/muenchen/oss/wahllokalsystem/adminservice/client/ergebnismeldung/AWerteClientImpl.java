package de.muenchen.oss.wahllokalsystem.adminservice.client.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.adminservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.ergebnismeldung.client.AWerteControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.AWerteClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
public class AWerteClientImpl implements AWerteClient {

    private final AWerteControllerApi awerteControllerApi;

    private final ExceptionFactory exceptionFactory;

    @Override
    public void initialiseAWerte(List<String> wahlbezirkIDs) {
        try {
            awerteControllerApi.initialiseAWerte(wahlbezirkIDs);
        } catch (final WlsException wlsException) {
            log.debug("#initialiseAWerte found WlsException:", wlsException);
            throw wlsException;
        } catch (final Exception exception) {
            log.error("Error beim A-Werte initialisieren: ", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_ERGEBNISMELDUNG);
        }
    }
}

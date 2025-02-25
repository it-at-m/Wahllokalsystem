package de.muenchen.oss.wahllokalsystem.adminservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.adminservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.client.WahlenControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.konfigurierterwahltag.WahlenClient;
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
public class WahlenClientImpl implements WahlenClient {

    private final ExceptionFactory exceptionFactory;

    private final WahlenControllerApi wahlenControllerApi;

    public void resetWahlen() {
        log.debug("#resetWahlen");
        try {
            wahlenControllerApi.resetWahlen();
        } catch (WlsException wlsException) {
            log.debug("#resetWahlen found WlsException:", wlsException);
            throw wlsException;
        } catch (Exception exception) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN);
        }
    }
}

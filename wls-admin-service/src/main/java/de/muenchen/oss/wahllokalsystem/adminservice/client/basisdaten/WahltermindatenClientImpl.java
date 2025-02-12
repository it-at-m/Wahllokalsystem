package de.muenchen.oss.wahllokalsystem.adminservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.adminservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.client.WahltermindatenControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahltermindatenClient;
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
public class WahltermindatenClientImpl implements WahltermindatenClient {

    private final ExceptionFactory exceptionFactory;

    private final WahltermindatenControllerApi wahltermindatenControllerApi;

    @Override
    public void putWahltermindaten(String wahltagID) throws WlsException {
        try {
            wahltermindatenControllerApi.putWahltermindaten(wahltagID);
        } catch (WlsException wlsException) {
            log.debug("#putWahltermindaten found WlsException:", wlsException);
            throw wlsException;
        } catch (Exception exception) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN);
        }
    }

    @Override
    public void deleteWahltermindaten(String wahltagID) throws WlsException {
        log.debug("#deleteWahltermindaten");

        try {
            wahltermindatenControllerApi.deleteWahltermindaten(wahltagID);
        } catch (WlsException wlsException) {
            log.debug("#deleteWahltermindaten found WlsException:", wlsException);
            throw wlsException;
        } catch (Exception exception) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN);
        }
    }
}

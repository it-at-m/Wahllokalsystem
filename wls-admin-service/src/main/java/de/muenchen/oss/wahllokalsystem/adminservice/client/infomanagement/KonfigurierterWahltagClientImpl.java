package de.muenchen.oss.wahllokalsystem.adminservice.client.infomanagement;

import de.muenchen.oss.wahllokalsystem.adminservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.client.KonfigurierterWahltagControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.KonfigurierterWahltagClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
    public void postKonfigurierterWahltag(KonfigurierterWahltagModel konfigurierterWahltag) {
        log.debug("#postKonfigurierterWahltag");

        try {
            val konfigurierterWahltagDTO = konfigurierterWahltagClientMapper.toClientDto(konfigurierterWahltag);
            konfigurierterWahltagControllerApi.setKonfigurierterWahltag(konfigurierterWahltagDTO);
        } catch (WlsException wlsEx) {
            log.debug("found WlsException: {}");
            throw wlsEx;
        } catch (Exception exception) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT);
        }
    }
}

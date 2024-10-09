package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.infomanagement;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.KonfigurierterWahltagClient;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KonfigurierterWahltagClientImpl implements KonfigurierterWahltagClient {

    private static final String INFOMANAGEMENT_SERVICE_DISCOVERY_NAME = "http://infomanagement:8080";

    private final ExceptionFactory exceptionFactory;

    @Autowired
    private OAuth2RestTemplate template;

    @Override
    @Timed(MonitoringConfig.CLIENT_REST)
    public KonfigurierterWahltagModel getKonfigurierterWahltag() {
        log.debug("#getKonfigurierterWahltag");

        KonfigurierterWahltagModel konfigurierterWahltag;
        try {
            konfigurierterWahltag = template.getForObject(INFOMANAGEMENT_SERVICE_DISCOVERY_NAME + "/businessActions/konfigurierterWahltag", KonfigurierterWahltagModel.class);
        } catch (WlsException wlsEx) {
            log.debug("found WlsException: {}");
            throw wlsEx;
        } catch (Exception exception) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT);
        }

        if (konfigurierterWahltag == null || konfigurierterWahltag.wahltag() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.INFOMANAGEMENT_WAHLTAG_NULL_OR_EMPTY);
        }
        return konfigurierterWahltag;
    }
}

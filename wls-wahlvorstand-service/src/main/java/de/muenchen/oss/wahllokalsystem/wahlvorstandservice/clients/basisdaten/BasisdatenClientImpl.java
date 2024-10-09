package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.basisdaten;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.BasisdatenClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BasisdatenClientImpl implements BasisdatenClient {

    private static final String BASISDATEN_SERVICE_DISCOVERY_NAME = "http://basisdaten:8080";

    private final ExceptionFactory exceptionFactory;

    @Autowired
    private OAuth2RestTemplate template;

    @Override
    @Timed(MonitoringConfig.CLIENT_REST)
    public List<Wahl> getWahlen(String wahltagID) throws WlsException {
        log.debug("#getWahlen");

        List<Wahl> wahlen;
        try {
            Wahl[] wahlArray = template.getForObject(BASISDATEN_SERVICE_DISCOVERY_NAME + "/businessActions/wahlen/" + wahltagID, Wahl[].class);
            if (wahlArray == null) {
                return null;
            }
            wahlen = Arrays.asList(wahlArray);
        } catch (WlsException wlsEx) {
            log.debug("found WlsException: {}");
            throw wlsEx;
        } catch (Exception exception) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN);
        }
        return wahlen;
    }
}

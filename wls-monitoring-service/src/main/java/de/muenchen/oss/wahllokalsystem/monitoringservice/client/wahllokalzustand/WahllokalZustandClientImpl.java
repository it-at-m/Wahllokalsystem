package de.muenchen.oss.wahllokalsystem.monitoringservice.client.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.client.WahllokalzustandControllerApi;
import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandClient;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
@RequiredArgsConstructor
@Slf4j
public class WahllokalZustandClientImpl implements WahllokalZustandClient {

    private final ExceptionFactory exceptionFactory;

    private final WahllokalzustandControllerApi wahllokalzustandControllerApi;
    private final WahllokalZustandClientMapper wahllokalZustandClientMapper;

    @Override
    public void postWahllokalZustand(WahllokalZustandModel wahllokalzustandModel) throws WlsException {
        val wahllokalZustandDTO = wahllokalZustandClientMapper.toDTO(wahllokalzustandModel);
        try {
            wahllokalzustandControllerApi.saveWahllokalZustand(wahllokalZustandDTO);
        } catch (final Exception exception) {
            log.info("exception on postWahllokalZustand from external", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI);
        }
    }
}

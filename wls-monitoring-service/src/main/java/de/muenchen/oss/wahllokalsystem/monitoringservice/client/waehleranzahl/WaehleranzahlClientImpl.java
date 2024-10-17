package de.muenchen.oss.wahllokalsystem.monitoringservice.client.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.monitoringservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.client.WahlbeteiligungControllerApi;
import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlClient;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlModel;
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
public class WaehleranzahlClientImpl implements WaehleranzahlClient {

    private final ExceptionFactory exceptionFactory;

    private final WahlbeteiligungControllerApi wahlbeteiligungControllerApi;
    private final WaehleranzahlClientMapper waehleranzahlClientMapper;

    @Override
    public void postWahlbeteiligung(WaehleranzahlModel waehleranzahlModel) throws WlsException {
        val wahlbeteiligungsMeldungDTO = waehleranzahlClientMapper.toDTO(waehleranzahlModel);
        try {
            wahlbeteiligungControllerApi.saveWahlbeteiligung(wahlbeteiligungsMeldungDTO);
        } catch (final Exception exception) {
            log.info("exception on postWahlbeteiligung from external", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI);
        }
    }
}

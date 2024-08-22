package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.client.WahldatenControllerApi;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkeClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
@RequiredArgsConstructor
@Slf4j
public class WahlbezirkeClientImpl implements WahlbezirkeClient {

    private final ExceptionFactory exceptionFactory;
    private final WahldatenControllerApi wahldatenControllerApi;
    private final WahlbezirkeClientMapper wahlbezirkeClientMapper;

    @Override
    public Set<WahlbezirkModel> loadWahlbezirke(LocalDate forDate, String wahltagNummer) throws WlsException {
        final Set<WahlbezirkDTO> wahlbezirkDTOS;
        try {
            wahlbezirkDTOS = wahldatenControllerApi.loadWahlbezirke(forDate, wahltagNummer);
        } catch (final Exception exception) {
            log.info("exception on loadWahlbezirke from external", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI);
        }
        if (wahlbezirkDTOS == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.NULL_FROM_CLIENT);
        }
        return wahlbezirkeClientMapper.fromRemoteSetOfDTOsToSetOfModels(wahlbezirkDTOS);
    }
}

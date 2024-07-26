package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.client.WahldatenControllerApi;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahl.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahl.WahlClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahl.WahlModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
@RequiredArgsConstructor
@Slf4j
public class WahlClientImpl implements WahlClient {

    private final ExceptionFactory exceptionFactory;

    private final WahldatenControllerApi wahldatenControllerApi;
    private final WahlClientMapper wahlClientMapper;

    @Override
    public List<WahlModel> getWahlen(java.time.LocalDate wahltag, String nummer) {
        final Set <WahlDTO> wahlDTO;
        try {
            wahlDTO = (Set<WahlDTO>) wahldatenControllerApi.loadWahlen(wahltag, nummer);
        } catch (final Exception exception) {
            log.info("exception on loadwahl from external", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI);
        }
        if (wahlDTO == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.NULL_FROM_CLIENT);
        }

        return wahlClientMapper.fromRemoteClientSetOfWahlDTOtoListOfWahlModel(wahlDTO);
    }
}

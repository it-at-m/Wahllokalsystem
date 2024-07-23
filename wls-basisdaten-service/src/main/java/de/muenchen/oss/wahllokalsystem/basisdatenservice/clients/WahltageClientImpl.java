package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.client.WahldatenControllerApi;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltagModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltageClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
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
public class WahltageClientImpl implements WahltageClient {

    private final ExceptionFactory exceptionFactory;

    private final WahldatenControllerApi wahldatenControllerApi;
    private final WahltageClientMapper wahltageClientMapper;

    @Override
    public List<WahltagModel> getWahltage(LocalDate tag) throws WlsException {

        final Set<WahltagDTO> wahltageDTO;
        try {
            wahltageDTO = wahldatenControllerApi.loadWahltageSinceIncluding(tag);
        } catch (final Exception exception) {
            log.info("exception on loadwahltage from external", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI);
        }
        if (wahltageDTO == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.NULL_FROM_CLIENT);
        }
        return wahltageClientMapper.fromRemoteClientSetOfWahltagDTOtoListOfWahltagModel(wahltageDTO);
    }
}

package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.api.WahlvorschlagControllerApi;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.model.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.WahlvorschlaegeClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.WahlvorschlaegeModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
@RequiredArgsConstructor
@Slf4j
public class ClientImpl implements WahlvorschlaegeClient {

    private final ExceptionFactory exceptionFactory;

    private final WahlvorschlagControllerApi wahlvorschlagControllerApi;
    private final WahlvorschlaegeClientMapper wahlvorschlaegeClientMapper;

    @Override
    public WahlvorschlaegeModel getWahlvorschlaege(final BezirkUndWahlID bezirkUndWahlID) {
        final WahlvorschlaegeDTO wahlvorschlaege;
        try {
            wahlvorschlaege = wahlvorschlagControllerApi.loadWahlvorschlaege(bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID());
        } catch (final Exception exception) {
            log.info("exception on loadwahlvorschlaege from external", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI);
        }
        if (wahlvorschlaege == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.NULL_FROM_CLIENT);
        }

        return wahlvorschlaegeClientMapper.toModel(wahlvorschlaege);
    }
}

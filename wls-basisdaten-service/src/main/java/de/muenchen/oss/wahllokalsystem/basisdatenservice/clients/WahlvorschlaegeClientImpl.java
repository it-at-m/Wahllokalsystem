package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.client.WahlvorschlagControllerApi;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumvorlagenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenReferenceModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlaegeClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlaegeModel;
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
public class WahlvorschlaegeClientImpl implements WahlvorschlaegeClient, ReferendumvorlagenClient {

    private final ExceptionFactory exceptionFactory;

    private final WahlvorschlagControllerApi wahlvorschlagControllerApi;
    private final WahlvorschlaegeClientMapper wahlvorschlaegeClientMapper;
    private final ReferendumvorlagenClientMapper referendumvorlagenClientMapper;

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

    @Override
    public ReferendumvorlagenModel getReferendumvorlagen(ReferendumvorlagenReferenceModel referendumvorlagenReferenceModel) {
        final ReferendumvorlagenDTO referendumvorlagen;
        try {
            referendumvorlagen = wahlvorschlagControllerApi.loadReferendumvorlagen(referendumvorlagenReferenceModel.wahlID(),
                    referendumvorlagenReferenceModel.wahlbezirkID());
        } catch (final Exception exception) {
            log.info("exception on loadrefendumvorlagen from external", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI);
        }
        if (referendumvorlagen == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.NULL_FROM_CLIENT);
        }
        return referendumvorlagenClientMapper.toModel(referendumvorlagen);
    }
}

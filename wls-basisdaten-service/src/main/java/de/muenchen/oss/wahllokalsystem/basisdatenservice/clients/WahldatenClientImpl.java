package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.client.WahldatenControllerApi;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.BasisdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.WahldatenClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
@RequiredArgsConstructor
@Slf4j
public class WahldatenClientImpl implements WahldatenClient {

    private final ExceptionFactory exceptionFactory;
    private final WahldatenControllerApi wahldatenControllerApi;
    private final WahldatenClientMapper wahldatenClientMapper;

    @Override
    public BasisdatenModel loadBasisdaten(LocalDate forDate, String withNummer) throws WlsException {

        final BasisdatenDTO basisdatenDTO;
        try {
            basisdatenDTO = wahldatenControllerApi.loadBasisdaten(forDate, withNummer);
        } catch (final Exception exception) {
            log.info("exception on getBasisdaten from external", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_SERVICE);
        }
        if (basisdatenDTO == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETKOPFDATEN_NO_BASISDATEN);
        }
        return wahldatenClientMapper.fromRemoteClientDTOToModel(basisdatenDTO);
    }
}

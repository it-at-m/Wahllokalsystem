package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.client.WahldatenControllerApi;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahltagWithNummer;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlenClient;
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
public class WahlenClientImpl implements WahlenClient {

    private final ExceptionFactory exceptionFactory;

    private final WahldatenControllerApi wahldatenControllerApi;
    private final WahlenClientMapper wahlenClientMapper;

    @Override
    public List<WahlModel> getWahlen(final WahltagWithNummer wahltagWithNummer) {
        final Set<WahlDTO> wahlDTOs;
        try {
            wahlDTOs = wahldatenControllerApi.loadWahlen(wahltagWithNummer.wahltag(), wahltagWithNummer.wahltagNummer());
        } catch (final Exception exception) {
            log.info("exception on loadwahl from external", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI);
        }
        if (wahlDTOs == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.NULL_FROM_CLIENT);
        }
        return wahlenClientMapper.fromRemoteClientSetOfWahlDTOtoListOfWahlModel(wahlDTOs);
    }
}

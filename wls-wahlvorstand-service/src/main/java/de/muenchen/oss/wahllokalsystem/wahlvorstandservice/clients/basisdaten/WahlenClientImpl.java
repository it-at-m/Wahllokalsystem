package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.basisdaten;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.aou.client.WahldatenControllerApi;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.WahlModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.WahlenClient;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.WahltagWithNummer;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
public class WahlenClientImpl implements WahlenClient {

    private final ExceptionFactory exceptionFactory;
    private final WahldatenControllerApi wahldatenControllerApi;
    private final WahlenClientMapper wahlenClientMapper;

    @Override
    public List<WahlModel> getWahlen(final WahltagWithNummer wahltagWithNummer) throws WlsException {
        final Set<WahlDTO> wahlDTOs;
        try {
            wahlDTOs = wahldatenControllerApi.loadWahlen(wahltagWithNummer.wahltag(), wahltagWithNummer.wahltagNummer());
        } catch (final Exception exception) {
            log.info("exception on loadwahl from external", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_AOUEAI);
        }
        if (wahlDTOs == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.AOUEAI_WAHLVORSTAND_NULL);
        }
        return wahlenClientMapper.fromRemoteClientSetOfWahlDTOtoListOfWahlModel(wahlDTOs);
    }
}

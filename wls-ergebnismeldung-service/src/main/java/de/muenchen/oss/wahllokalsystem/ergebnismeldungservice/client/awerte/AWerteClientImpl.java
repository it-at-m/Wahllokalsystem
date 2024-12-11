package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.client.WahldatenControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteClient;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
@RequiredArgsConstructor
@Slf4j
public class AWerteClientImpl implements AWerteClient {

    private final WahldatenControllerApi wahldatenControllerApi;

    private final AWerteClientMapper aWerteClientMapper;

    @Override
    public List<AWerteModel> getAWerte(final String wahlbezirkID) {
        final List<WahlberechtigteDTO> wahlberechtigteDTOSet;
        try {
            wahlberechtigteDTOSet = wahldatenControllerApi.loadWahlberechtigte(wahlbezirkID);
        } catch (final Exception exception) {
            log.info("exception on getAWerte from external", exception);
            return null;
        }
        return aWerteClientMapper.fromRemoteClientListOfWahlberechtigteDtoToListOfAWerteModel(wahlberechtigteDTOSet);
    }
}

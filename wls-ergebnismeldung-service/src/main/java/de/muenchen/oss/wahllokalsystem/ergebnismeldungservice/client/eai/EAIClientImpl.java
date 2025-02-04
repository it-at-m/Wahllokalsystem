package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.eai;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.client.WahldatenControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.client.WahlergebnisControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteClient;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.EaiService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
@RequiredArgsConstructor
@Slf4j
public class EAIClientImpl implements AWerteClient, EaiService {

    private final WahldatenControllerApi wahldatenControllerApi;
    private final WahlergebnisControllerApi wahlergebnisControllerApi;

    private final AWerteClientMapper aWerteClientMapper;

    private final ExceptionFactory exceptionFactory;

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

    @Override
    public void sendErgebnismeldung(final ErgebnismeldungDTO ergebnismeldungDTO) {
        try {
            wahlergebnisControllerApi.saveErgebnismeldung(ergebnismeldungDTO);
        } catch (final Exception exception) {
            log.warn("#saveWahlergebnismeldung: Ergebnismeldung ist nicht validiert. Exception: {}", exception.getMessage());
            simLogging(ergebnismeldungDTO.getMeldungsart());
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_AOUEAI);
        }
    }

    private void simLogging(final ErgebnismeldungDTO.MeldungsartEnum meldungsart) {
        try {
            switch (meldungsart) {
            case NIEDERSCHRIFT:
                MDC.put("eid", "NIEDERSCHRIFT_GESENDET");
                break;
            case SCHNELLMELDUNG:
                MDC.put("eid", "SCHNELLMELDUNG_GESENDET");
                break;
            }
            MDC.put("result", "3");
            log.info("IVU AOUEAI nicht erreichbar!");
        } finally {
            MDC.remove("eid");
            MDC.remove("result");
        }
    }
}

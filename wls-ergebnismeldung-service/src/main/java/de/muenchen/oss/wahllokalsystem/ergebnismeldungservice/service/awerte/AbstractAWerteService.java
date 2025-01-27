package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractAWerteService {

    private final AWerteRepository aWerteRepository;

    private final AWerteValidator aWerteValidator;

    private final AWerteModelMapper aWerteModelMapper;

    private final AWerteClient aWerteClient;

    private final ExceptionFactory exceptionFactory;

    protected List<AWerteModel> getAWerte(String wahlbezirkID) {
        log.info("#getAWerte for wahlbezirkID={}", wahlbezirkID);

        aWerteValidator.validWahlbezirkIDParamOrThrow(wahlbezirkID);

        List<AWerteModel> aWerteList = aWerteClient.getAWerte(wahlbezirkID);

        if (aWerteList != null && !aWerteList.isEmpty()) {
            try {
                aWerteRepository.saveAll(aWerteModelMapper.fromListOfAWerteModelToListOfAWerteEntity(aWerteList));
            } catch (Exception e) {
                log.error("#getAWerte unsaveable: " + e.getMessage(), e);
            }
        } else {
            log.info("Liefere 'alte' A-Werte, weil der Client keine Antwort liefern konnte.");
            aWerteList = aWerteModelMapper.fromListOfAWerteEntityToListOfAWerteModel(aWerteRepository.findByBezirkUndWahlID_WahlbezirkID(wahlbezirkID));

            if (aWerteList == null || aWerteList.isEmpty()) {
                log.error("#getAWerte Keine Daten erhalten und keine gespeicherten A-Werte vorhanden!");
                throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.GETAWERTE_UNSAVEABLE);
            }
        }
        return aWerteList;
    }
}

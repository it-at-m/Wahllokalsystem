package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AWerteService {

    public static final Pattern pattern = Pattern.compile("BEZIRK-(.*)\",\"wahlterminId");

    private final AWerteRepository aWerteRepository;

    private final AWerteValidator aWerteValidator;

    private final AWerteModelMapper aWerteModelMapper;

    private final AWerteClient aWerteClient;

    private final ExceptionFactory exceptionFactory;

    private final AsyncProgress asyncProgress;

    @PreAuthorize("hasAuthority('Ergebnismeldung_BUSINESSACTION_GetAWerte') OR hasAuthority('Admin_BUSINESSACTION_LoadWahltermindaten')")
    public List<AWerteModel> getAWerte(String wahlbezirkID) {
        log.info("#getAWerte for wahlbezirkID={}", wahlbezirkID);

        aWerteValidator.validWahlbezirkIDParamOrThrow(wahlbezirkID);

        List<AWerteModel> aWerteList = aWerteClient.getAWerte(wahlbezirkID);

        if (aWerteList != null && !aWerteList.isEmpty()) {
            try {
                aWerteRepository.saveAll(aWerteModelMapper.fromListOfAWerteModeltoListOfAWerteEntity(aWerteList));
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

    @Async
    @PreAuthorize("hasAuthority('Admin_BUSINESSACTION_LoadWahltermindaten')")
    public void initialiseAWerte(List<String> wahlbezirkIDs) {
        log.info("Initialisier A-Werte für {} Wahllokale", wahlbezirkIDs.size());
        asyncProgress.reset(wahlbezirkIDs.size());
        for (int i = 0; i < wahlbezirkIDs.size(); i++) {
            final String wbzID = wahlbezirkIDs.get(i);
            try {
                // improve this, see issue #596
                try {
                    String decodedWbzID = new String(Base64.getDecoder().decode(wbzID.getBytes()));
                    Matcher wbzIdMatcher = pattern.matcher(decodedWbzID);
                    if (wbzIdMatcher.find()) {
                        asyncProgress.setAWerteNext(wbzIdMatcher.group(1));
                    } else {
                        asyncProgress.setAWerteNext(decodedWbzID);
                    }
                } catch (Exception e) {
                    asyncProgress.setAWerteNext(wbzID);
                }
                getAWerte(wbzID);
                asyncProgress.incAWerteFinished();
                log.info("A-Werte für Wahllokal {} erfolgreich geladen. ({}/{})", wbzID, i + 1, wahlbezirkIDs.size());
            } catch (Exception e) {
                e.printStackTrace();
                log.info("A-Werte für Wahllokal {} konnten nicht geladen werden: {}. ({}/{})", wbzID, e.getMessage(),
                        i + 1, wahlbezirkIDs.size());
            }
        }
        asyncProgress.setAWerteLoadingActive(false);
    }
}

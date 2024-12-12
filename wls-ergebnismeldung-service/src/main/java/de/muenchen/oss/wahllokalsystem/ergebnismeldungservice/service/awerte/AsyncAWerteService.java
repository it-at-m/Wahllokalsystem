package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AsyncAWerteService extends AbstractAWerteService {

    public static final Pattern pattern = Pattern.compile("BEZIRK-(.*)\",\"wahlterminId");

    private final AsyncProgress asyncProgress;

    public AsyncAWerteService(AWerteRepository aWerteRepository,
            AWerteValidator aWerteValidator, AWerteModelMapper aWerteModelMapper, AWerteClient aWerteClient,
            ExceptionFactory exceptionFactory, AsyncProgress asyncProgress) {
        super(aWerteRepository, aWerteValidator, aWerteModelMapper, aWerteClient, exceptionFactory);
        this.asyncProgress = asyncProgress;
    }

    @Async
    @PreAuthorize("hasAuthority('Admin_BUSINESSACTION_LoadWahltermindaten')")
    public void initialiseAWerte(List<String> wahlbezirkIDs) {
        log.info("Initialisiere A-Werte für {} Wahllokale", wahlbezirkIDs.size());
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

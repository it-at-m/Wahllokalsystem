package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.KonfigurationRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KonfigurationService {

    private final KonfigurationRepository konfigurationRepository;

    private final KonfigurationModelMapper konfigurationModelMapper;

    public void getKonfiguration(final KonfigurationKonfigKey konfigurationKonfigKey) {
        val wahlbezirkArtOfRequest = getWahlbezirkArt();
        log.info("wahlbezirkArtOfRequest > {}", wahlbezirkArtOfRequest);
        val schluessel = konfigurationModelMapper.getAlternativKey(konfigurationKonfigKey, wahlbezirkArtOfRequest);
    }

    private WahlbezirkArt getWahlbezirkArt() {
        try {
            Map details = (Map) SecurityContextHolder.getContext().getAuthentication().getDetails();
            return WahlbezirkArt.valueOf((String) details.get("wahlbezirksArt"));
        } catch (Exception e) {
            log.error("#getKonfiguration Error: Wahlbezirkart konnte nicht erkannt werden. UWB wurde als Standardwert angenommen");
            return WahlbezirkArt.UWB;
        }
    }
}

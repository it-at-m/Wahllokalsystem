package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.common.JWTService;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.KonfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KonfigurationService {

    private final KonfigurationRepository konfigurationRepository;

    private final KonfigurationModelMapper konfigurationModelMapper;

    private final JWTService jwtService;

    public void getKonfiguration(final KonfigurationKonfigKey konfigurationKonfigKey) {
        val wahlbezirkArtOfRequest = getWahlbezirkArt();
        log.info("wahlbezirkArtOfRequest > {}", wahlbezirkArtOfRequest);
        val schluessel = konfigurationModelMapper.getAlternativKey(konfigurationKonfigKey, wahlbezirkArtOfRequest);
    }

    private WahlbezirkArt getWahlbezirkArt() {
        return WahlbezirkArt.valueOf(jwtService.getDetail("wahlbezirksArt").orElseGet(() -> {
            log.error("#getKonfiguration Error: Wahlbezirkart konnte nicht erkannt werden. UWB wurde als Standardwert angenommen");
            return WahlbezirkArt.UWB.name();
        }));
    }
}

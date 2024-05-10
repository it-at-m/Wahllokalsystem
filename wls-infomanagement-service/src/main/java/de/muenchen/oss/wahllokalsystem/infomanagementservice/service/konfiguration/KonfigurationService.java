package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.common.JWTService;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.KonfigurationRepository;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KonfigurationService {

    private final KonfigurationRepository konfigurationRepository;

    private final KonfigurationModelMapper konfigurationModelMapper;

    private final KonfigurationModelValidator konfigurationModelValidator;

    private final JWTService jwtService;

    @PreAuthorize("hasAuthority('Infomanagement_BUSINESSACTION_GetKonfiguration')")
    public Optional<KonfigurationModel> getKonfiguration(@NotNull final KonfigurationKonfigKey konfigurationKonfigKey) {
        log.info("#getKonfiguration");

        konfigurationModelValidator.valideOrThrowGetKonfigurationByKey(konfigurationKonfigKey);

        val wahlbezirkArtOfRequest = getWahlbezirkArt();
        val alternativKey = konfigurationModelMapper.getAlternativKey(konfigurationKonfigKey, wahlbezirkArtOfRequest);

        val repositoryLookupKey = alternativKey.orElse(konfigurationKonfigKey);
        val konfigurationFromRepo = konfigurationRepository.findById(repositoryLookupKey.name());

        return konfigurationFromRepo.map(konfigurationModelMapper::toModel);
    }

    private WahlbezirkArt getWahlbezirkArt() {
        return WahlbezirkArt.valueOf(jwtService.getDetail("wahlbezirksArt").orElseGet(() -> {
            log.error("#getKonfiguration Error: Wahlbezirkart konnte nicht erkannt werden. UWB wurde als Standardwert angenommen");
            return WahlbezirkArt.UWB.name();
        }));
    }
}

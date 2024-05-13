package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.common.security.AuthenticationHandler;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.KonfigurationRepository;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KonfigurationService {

    private static final WahlbezirkArt WAHLBEZIRK_ART_FALLBACK = WahlbezirkArt.UWB;
    private static final String WAHLBEZIRK_ART_USER_DETAIL_KEY = "wahlbezirksArt";

    private final KonfigurationRepository konfigurationRepository;

    private final KonfigurationModelMapper konfigurationModelMapper;

    private final KonfigurationModelValidator konfigurationModelValidator;

    private final Collection<AuthenticationHandler> authenticationHandlers;

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
        val currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        val authenticationHandler = authenticationHandlers.stream().filter(handler -> handler.canHandle(currentAuthentication)).findFirst();
        if (authenticationHandler.isPresent()) {
            val wahlbezirkOfUser = authenticationHandler.get().getDetail(WAHLBEZIRK_ART_USER_DETAIL_KEY, currentAuthentication);
            return wahlbezirkOfUser.map(WahlbezirkArt::valueOf).orElseGet(() -> {
                log.error("#getKonfiguration Error: Wahlbezirkart konnte nicht erkannt werden. UWB wurde als Standardwert angenommen");
                return WAHLBEZIRK_ART_FALLBACK;
            });
        } else {
            log.error("kein handler für authentication class {} vorhanden. Verwende Wahlbezirksart-Fallback {}", currentAuthentication.getClass(),
                    WAHLBEZIRK_ART_FALLBACK);
            return WAHLBEZIRK_ART_FALLBACK;
        }
    }
}

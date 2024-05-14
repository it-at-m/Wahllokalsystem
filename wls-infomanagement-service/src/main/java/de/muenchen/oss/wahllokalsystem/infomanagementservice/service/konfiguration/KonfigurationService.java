package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.common.security.AuthenticationHandler;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.KonfigurationRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KonfigurationService {

    private static final WahlbezirkArt WAHLBEZIRK_ART_FALLBACK = WahlbezirkArt.UWB;
    private static final String WAHLBEZIRK_ART_USER_DETAIL_KEY = "wahlbezirksArt";

    private static final String CODE_POSTKONFIGURATION_NOT_SAVEABLE = "101";
    private static final String MSG_POSTKONFIGURATION_NOT_SAVEABLE = "postKonfiguration: Die Konfiguration konnte nicht gespeichert werden";

    private final KonfigurationRepository konfigurationRepository;

    private final KonfigurationModelMapper konfigurationModelMapper;

    private final KonfigurationModelValidator konfigurationModelValidator;

    private final Collection<AuthenticationHandler> authenticationHandlers;

    private final ServiceIDFormatter serviceIDFormatter;

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

    @PreAuthorize("hasAuthority('Infomanagement_BUSINESSACTION_GetKonfigurationen')")
    public List<KonfigurationModel> getAllKonfigurations() {
        log.info("#getKonfigurationen");

        return konfigurationRepository.findAll().stream().map(konfigurationModelMapper::toModel).toList();
    }

    @PreAuthorize("hasAuthority('Infomanagement_BUSINESSACTION_PostKonfiguration')")
    public void setKonfiguration(@NotNull final KonfigurationSetModel konfigurationSetModel) {
        log.info("#postKonfiguration");

        konfigurationModelValidator.valideOrThrowSetKonfiguration(konfigurationSetModel);

        val entityToSave = konfigurationModelMapper.toEntity(konfigurationSetModel);

        try {
            konfigurationRepository.save(entityToSave);
        } catch (final Exception onSaveException) {
            log.error("#setKonfiguration unsaveable: ", onSaveException);
            throw TechnischeWlsException.withCode(CODE_POSTKONFIGURATION_NOT_SAVEABLE).inService(serviceIDFormatter.getId())
                    .buildWithMessage(MSG_POSTKONFIGURATION_NOT_SAVEABLE);
        }
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

package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.Wahlvorstand;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.WahlvorstandRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.Wahlvorstandsmitglied;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.security.AuthenticationHandler;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WahlvorstandService {

    private final WahlvorstandValidator wahlvorstandValidator;
    private final WahlvorstandModelMapper wahlvorstandModelMapper;
    private final WahlvorstandRepository wahlvorstandRepository;
    private final WahlvorstandEaiClient wahlvorstandEaiClient;
    private final KonfigurierterWahltagClient konfigurierterWahltagClient;
    private final WahlenClient wahlenClient;
    private final ExceptionFactory exceptionFactory;
    private final FunktionsnamenMapping namenMapping;
    private final Collection<AuthenticationHandler> authenticationHandlers;

    private static final WahlbezirkArt WAHLBEZIRK_ART_FALLBACK = WahlbezirkArt.UWB;

    @PreAuthorize(
        "hasAuthority('Wahlvorstand_BUSINESSACTION_GetWahlvorstand')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)"
    )
    public Optional<WahlvorstandModel> getWahlvorstand(@P("wahlbezirkID") final String wahlbezirkID) {
        log.info("#getWahlvorstand");
        wahlvorstandValidator.validWahlbezirkIDOrThrow(wahlbezirkID);
        return wahlvorstandRepository.findById(wahlbezirkID).map(wahlvorstandModelMapper::toModel);
    }

    @PreAuthorize("hasAuthority('Wahlvorstand_BUSINESSACTION_UpdateWahlvorstand')")
    public Optional<WahlvorstandModel> updateWahlvorstand(@P("wahlbezirkID") final String wahlbezirkID) {
        log.info("#updateWahlvorstand");
        wahlvorstandValidator.validWahlbezirkIDOrThrow(wahlbezirkID);
        KonfigurierterWahltagModel konfigurierterWahltagModel = konfigurierterWahltagClient.getKonfigurierterWahltag();
        WahlvorstandModel wahlvorstand = wahlvorstandEaiClient.getWahlvorstand(wahlbezirkID, konfigurierterWahltagModel.wahltag());
        return Optional.of(persistWahlvorstand(wahlvorstand, konfigurierterWahltagModel));
    }

    @Transactional
    @PreAuthorize("hasAuthority('Wahlvorstand_BUSINESSACTION_PostWahlvorstand')")
    public void postWahlvorstand(@P("param") WahlvorstandModel wahlvorstandModel) {
        log.info("#postWahlvorstand");
        wahlvorstandValidator.validWahlvorstandOrThrow(wahlvorstandModel);

        try {
            wahlvorstandRepository.save(wahlvorstandModelMapper.toEntity(wahlvorstandModel));
        } catch (Exception e) {
            log.error("#postWahlvorstand unsaveable: ", e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.POSTWAHLVORSTAND_NOT_SAVEABLE);
        }
        wahlvorstandEaiClient.postWahlvorstand(wahlvorstandModel);
    }

    public Optional<WahlvorstandModel> getFallbackWahlvorstand(String wahlbezirkID) {
        WahlvorstandModel fallbackWahlvorstand = WahlvorstandModel.builder().wahlbezirkID(wahlbezirkID).wahlvorstandsmitglieder(new ArrayList<>()).build();

        Arrays.stream(FunktionModel.values()).forEach(funktion -> {
            WahlvorstandsmitgliedModel mitglied = WahlvorstandsmitgliedModel.builder()
                    .identifikator("FALLBACK_" + funktion + wahlbezirkID)
                    .funktion(funktion)
                    .familienname("______________")
                    .vorname("______________")
                    .build();
            fallbackWahlvorstand.wahlvorstandsmitglieder().add(mitglied);
        });

        KonfigurierterWahltagModel konfigurierterWahltagModel = konfigurierterWahltagClient.getKonfigurierterWahltag();
        return Optional.of(persistWahlvorstand(fallbackWahlvorstand, konfigurierterWahltagModel));
    }

    private WahlvorstandModel persistWahlvorstand(WahlvorstandModel wahlvorstand, KonfigurierterWahltagModel konfigurierterWahltagModel) {
        if (wahlvorstand == null || wahlvorstand.wahlvorstandsmitglieder().isEmpty()) {
            return null;
        }

        val wahlvorstandDB = wahlvorstandRepository.findById(wahlvorstand.wahlbezirkID());
        if (wahlvorstandDB.isPresent()) {
            try {
                wahlvorstandRepository.save(populateFunktionsnameOffline(wahlvorstandModelMapper.toEntity(wahlvorstand), wahlvorstandDB.get()));
            } catch (IllegalStateException ex) {
                wahlvorstandRepository.save(populateFunktionsnameOnline(wahlvorstandModelMapper.toEntity(wahlvorstand), konfigurierterWahltagModel));
            }
        } else {
            wahlvorstandRepository.save(populateFunktionsnameOnline(wahlvorstandModelMapper.toEntity(wahlvorstand), konfigurierterWahltagModel));
        }
        return wahlvorstand;
    }

    private Wahlvorstand populateFunktionsnameOffline(Wahlvorstand wahlvorstand, Wahlvorstand wahlvorstandDB) throws IllegalStateException {
        List<Wahlvorstandsmitglied> collect = wahlvorstand.getWahlvorstandsmitglieder().stream()
                .map(mitglied -> populateWahlvorstandsmitgliedFunktionsnameOffline(mitglied, wahlvorstandDB))
                .toList();
        wahlvorstand.setWahlvorstandsmitglieder(collect);
        return wahlvorstand;
    }

    private Wahlvorstandsmitglied populateWahlvorstandsmitgliedFunktionsnameOffline(Wahlvorstandsmitglied mitglied, Wahlvorstand wahlvorstandDB)
            throws IllegalStateException {
        Wahlvorstandsmitglied mitgliedDB = wahlvorstandDB.getWahlvorstandsmitglieder().stream()
                .filter(wahlvorstandsmitglied -> wahlvorstandsmitglied.getFunktion().equals(mitglied.getFunktion()))
                .findFirst()
                .orElse(null);
        if (mitgliedDB != null) {
            mitglied.setFunktionsname(mitgliedDB.getFunktionsname());
        } else {
            throw new IllegalStateException("Bisher unbekannte Funktion in Wahlvorstand gefunden. Muss neu gemapt werden.");
        }
        return mitglied;
    }

    private Wahlvorstand populateFunktionsnameOnline(Wahlvorstand wahlvorstand, KonfigurierterWahltagModel wahltagModel) {
        WahlbezirkArt wahlbezirkArt = getWahlbezirkArt();

        List<WahlModel> wahlen = wahlenClient.getWahlen(wahltagModel);
        if (wahlen == null) throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.BASISDATEN_ANTWORT_NULL);
        Wahlart zuerstAuszuzaehlendeWahl = wahlen.get(0).wahlart();

        List<Wahlvorstandsmitglied> collect = wahlvorstand.getWahlvorstandsmitglieder().stream()
                .map(mitglied -> populateWahlvorstandsmitgliedFunktionsnameOnline(mitglied, zuerstAuszuzaehlendeWahl, wahlbezirkArt))
                .toList();
        wahlvorstand.getWahlvorstandsmitglieder().clear();
        wahlvorstand.getWahlvorstandsmitglieder().addAll(collect);
        return wahlvorstand;
    }

    private Wahlvorstandsmitglied populateWahlvorstandsmitgliedFunktionsnameOnline(
            Wahlvorstandsmitglied mitglied, Wahlart wahlart, WahlbezirkArt wahlbezirkArt) {
        StringBuilder funktionsBuilder = new StringBuilder();
        String thisFunktion = getFunktion(wahlbezirkArt, mitglied, wahlart);
        if (thisFunktion == null || thisFunktion.isEmpty()) {
            funktionsBuilder.append(mitglied.getFunktion());
        } else {
            funktionsBuilder.append(thisFunktion);
        }
        mitglied.setFunktionsname(funktionsBuilder.toString());
        return mitglied;
    }

    private String getFunktion(WahlbezirkArt wahlbezirkArt, Wahlvorstandsmitglied mitglied, Wahlart wahlart) {
        String funktion = "";
        val mappings = getMappings(wahlbezirkArt);

        if (mappings != null) {
            Map<String, String> wahlartMapping = mappings.get(wahlart.name());
            if (wahlartMapping != null) {
                funktion = wahlartMapping.get(mitglied.getFunktion().name());
            }
        }
        return funktion;
    }

    private Map<String, Map<String, String>> getMappings(WahlbezirkArt wahlbezirkArt) {
        Map<WahlbezirkArt, Map<String, Map<String, String>>> funktionsMap = new EnumMap<>(WahlbezirkArt.class);
        funktionsMap.put(WahlbezirkArt.UWB, namenMapping.getUwbFunktion());
        funktionsMap.put(WahlbezirkArt.BWB, namenMapping.getBwbFunktion());
        return funktionsMap.get(wahlbezirkArt);
    }

    private WahlbezirkArt getWahlbezirkArt() {
        val currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        val authenticationHandler = authenticationHandlers.stream().filter(handler -> handler.canHandle(currentAuthentication)).findFirst();
        if (authenticationHandler.isPresent()) {
            val wahlbezirkOfUser = authenticationHandler.get().getDetail("wahlbezirksArt", currentAuthentication);
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

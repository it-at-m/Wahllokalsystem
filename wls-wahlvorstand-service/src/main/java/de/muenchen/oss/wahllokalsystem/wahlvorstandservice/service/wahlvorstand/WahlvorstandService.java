package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.Wahlvorstand;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.WahlvorstandRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.Wahlvorstandsmitglied;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.security.AuthenticationHandler;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.aoueaiClient.WahlvorstandEaiClient;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.aoueaiClient.WahlvorstandModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.basisdatenClient.WahlModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.basisdatenClient.Wahlart;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.basisdatenClient.WahlbezirkArt;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.basisdatenClient.WahlenClient;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.infomanagementClient.KonfigurierterWahltagClient;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.infomanagementClient.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.mapping.BWBFunktionsnamenMapping;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.mapping.UWBFunktionsnamenMapping;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    private final UWBFunktionsnamenMapping uwbNamenMapping;
    private final BWBFunktionsnamenMapping bwbNamenMapping;
    private final Collection<AuthenticationHandler> authenticationHandlers;

    private static final WahlbezirkArt WAHLBEZIRK_ART_FALLBACK = WahlbezirkArt.UWB;

    @PreAuthorize(
        "hasAuthority('Wahlvorstand_BUSINESSACTION_GetWahlvorstand')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)"
    )
    public WahlvorstandModel getWahlvorstand(@P("wahlbezirkID") final String wahlbezirkID) {
        log.info("#getWahlvorstand");
        wahlvorstandValidator.validWahlbezirkIDOrThrow(wahlbezirkID);
        return wahlvorstandModelMapper.toModel(wahlvorstandRepository.findByWahlbezirkID(wahlbezirkID));
    }

    @PreAuthorize("hasAuthority('Wahlvorstand_BUSINESSACTION_UpdateWahlvorstand')")
    public WahlvorstandModel updateWahlvorstand(@P("wahlbezirkID") final String wahlbezirkID) {
        log.info("#updateWahlvorstand");
        wahlvorstandValidator.validWahlbezirkIDOrThrow(wahlbezirkID);
        KonfigurierterWahltagModel konfigurierterWahltagModel = konfigurierterWahltagClient.getKonfigurierterWahltag();
        WahlvorstandModel wahlvorstand = wahlvorstandEaiClient.getWahlvorstand(wahlbezirkID, konfigurierterWahltagModel.wahltag());
        return persistWahlvorstand(wahlvorstand, konfigurierterWahltagModel);
    }

    public WahlvorstandModel getFallbackWahlvorstand(String wahlbezirkID) {
        WahlvorstandModel fallbackWahlvorstand = WahlvorstandModel.builder().wahlbezirkID(wahlbezirkID).build();

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
        return persistWahlvorstand(fallbackWahlvorstand, konfigurierterWahltagModel);
    }

    private WahlvorstandModel persistWahlvorstand(WahlvorstandModel wahlvorstand, KonfigurierterWahltagModel konfigurierterWahltagModel) {
        if (wahlvorstand == null || wahlvorstand.wahlvorstandsmitglieder().isEmpty()) {
            return null;
        }

        Wahlvorstand wahlvorstandDB = wahlvorstandRepository.findByWahlbezirkID(wahlvorstand.wahlbezirkID());
        if (wahlvorstandDB != null) {
            try {
                wahlvorstandRepository.save(populateFunktionsnameOffline(wahlvorstandModelMapper.toEntity(wahlvorstand), wahlvorstandDB));
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
        Map<String, Map<String, String>> mappings = null;
        if (wahlbezirkArt.equals(WahlbezirkArt.UWB)) {
            mappings = uwbNamenMapping.getUwbFunktion();
        } else if (wahlbezirkArt.equals(WahlbezirkArt.BWB)) {
            mappings = bwbNamenMapping.getBwbFunktion();
        }

        if (mappings != null) {
            Map<String, String> wahlartMapping = mappings.get(wahlart.name());
            if (wahlartMapping != null) {
                funktion = wahlartMapping.get(mitglied.getFunktion().name());
            }
        }
        return funktion;
    }

    private WahlbezirkArt getWahlbezirkArt() {
        val currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        val authenticationHandler = authenticationHandlers.stream().filter(handler -> handler.canHandle(currentAuthentication)).findFirst();
        if (authenticationHandler.isPresent()) {
            val wahlbezirkOfUser = authenticationHandler.get().getDetail("wahlbezirksArt", currentAuthentication); // todo: stimmt "wahlbezirksArt"?
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

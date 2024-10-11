package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.model.Wahlart;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.common.security.AuthenticationHandler;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.Funktion;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.Wahlvorstand;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.WahlvorstandRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.Wahlvorstandsmitglied;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.mapping.BWBFunktionsnamenmapping;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.mapping.UWBFunktionsamenMapping;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.model.WahlbezirkArt;
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

    private static final WahlbezirkArt WAHLBEZIRK_ART_FALLBACK = WahlbezirkArt.UWB;
    private static final String WAHLBEZIRK_ART_USER_DETAIL_KEY = "wahlbezirksArt";

    private final WahlvorstandRepository wahlvorstandRepository;
    private final WahlvorstandValidator wahlvorstandValidator;

    private final WahlvorstandModelMapper wahlvorstandModelMapper;
    private final ExceptionFactory exceptionFactory;
    private final KonfigurierterWahltagClient konfigurierterWahltagClient;
    private final WahlvorstandEaiClient wahlvorstandEaiClient;
    private final WahlenClient wahlenClient;

    private final UWBFunktionsamenMapping uwbNamenMapping;
    private final BWBFunktionsnamenmapping bwbNamenMapping;

    private final Collection<AuthenticationHandler> authenticationHandlers;


    @PreAuthorize("hasAuthority('Wahlvorstand_BUSINESSACTION_GetWahlvorstand')"
            + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)")
    public WahlvorstandModel getWahlvorstand(@P("wahlbezirkID") final String wahlbezirkID) {
        log.info("#getWahlvorstand");
        wahlvorstandValidator.validWahlbezirkIDOrThrow(wahlbezirkID);

        return wahlvorstandModelMapper.toModel(wahlvorstandRepository.findByWahlbezirkID(wahlbezirkID));
    }

    @PreAuthorize("hasAuthority('Wahlvorstand_BUSINESSACTION_PostWahlvorstand')")
    public void postWahlvorstand(@P("param") WahlvorstandModel wahlvorstandModel) {
        log.info("#postWahlvorstand");
        wahlvorstandValidator.validWahlvorstandAndWahlbezirkIDOrThrow(wahlvorstandModel);

        try {
            wahlvorstandRepository.save(wahlvorstandModelMapper.toEntity(wahlvorstandModel));
        } catch (Exception e) {
            log.error("#postWahlvorstand unsaveable: " + e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.POSTWAHLVORSTAND_NOT_SAVEABLE);
        }

        KonfigurierterWahltagModel wahltagModel = konfigurierterWahltagClient.getKonfigurierterWahltag();
        wahlvorstandEaiClient.postWahlvorstand(wahlvorstandModelMapper.toEntity(wahlvorstandModel), wahltagModel.wahltag());
    }

    @PreAuthorize("hasAuthority('Wahlvorstand_BUSINESSACTION_UpdateWahlvorstand')")
    public WahlvorstandModel updateWahlvorstand(@P("wahlbezirkID") final String wahlbezirkID) {
        log.info("#updateWahlvorstand");
        wahlvorstandValidator.validWahlbezirkIDOrThrow(wahlbezirkID);

        KonfigurierterWahltagModel wahltagModel = konfigurierterWahltagClient.getKonfigurierterWahltag();
        // todo: warum ist das rot
        Wahlvorstand wahlvorstand = wahlvorstandEaiClient.getWahlvorstand(wahlbezirkID, wahltagModel.wahltag());

        return wahlvorstandModelMapper.toModel(persistWahlvorstand(wahlvorstand, wahltagModel));
    }

    public WahlvorstandModel getFallbackWahlvorstand(String wahlbezirkID) {
        Wahlvorstand fallbackWahlvorstand = new Wahlvorstand();
        fallbackWahlvorstand.setWahlbezirkID(wahlbezirkID);

        Arrays.stream(Funktion.values()).forEach(funktion -> {
            Wahlvorstandsmitglied mitglied = new Wahlvorstandsmitglied();
            mitglied.setIdentifikator("FALLBACK_" + funktion + wahlbezirkID);
            mitglied.setFunktion(funktion);
            mitglied.setFamilienname("______________");
            mitglied.setVorname("______________");
            fallbackWahlvorstand.getWahlvorstandsmitglieder().add(mitglied);
        });

        KonfigurierterWahltagModel wahltagModel = konfigurierterWahltagClient.getKonfigurierterWahltag();
        return wahlvorstandModelMapper.toModel(persistWahlvorstand(fallbackWahlvorstand, wahltagModel));
    }

    private Wahlvorstand persistWahlvorstand(Wahlvorstand wahlvorstand, KonfigurierterWahltagModel wahltagModel) {
        if(wahlvorstand == null || wahlvorstand.getWahlvorstandsmitglieder().isEmpty()) {
            return null;
        }

        Wahlvorstand wahlvorstandDB = wahlvorstandRepository.findByWahlbezirkID(wahlvorstand.getWahlbezirkID());
        if (wahlvorstandDB != null) {
            try {
                wahlvorstandRepository.save(populateFunktionsnameOffline(wahlvorstand, wahlvorstandDB));
            } catch (IllegalStateException ex) {
                wahlvorstandRepository.save(populateFunktionsnameOnline(wahlvorstand, wahltagModel));
            }
        } else {
            wahlvorstandRepository.save(populateFunktionsnameOnline(wahlvorstand, wahltagModel));
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

    private Wahlvorstandsmitglied populateWahlvorstandsmitgliedFunktionsnameOffline(Wahlvorstandsmitglied mitglied, Wahlvorstand wahlvorstandDB) throws IllegalStateException {
        Wahlvorstandsmitglied mitgliedDB = wahlvorstandDB.getWahlvorstandsmitglieder().stream()
                .filter(wahlvorstandsmitglied -> wahlvorstandsmitglied.getFunktion().equals(mitglied.getFunktion()))
                .findFirst()
                .orElse(null);
        if (mitgliedDB != null) {
            mitglied.setFunktionsName(mitgliedDB.getFunktionsName());
        } else {
            throw new IllegalStateException("Bisher unbekannte Funktion in Wahlvorstand gefunden. Muss neu gemapt werden.");
        }
        return mitglied;
    }

    private Wahlvorstand populateFunktionsnameOnline(Wahlvorstand wahlvorstand, KonfigurierterWahltagModel wahltagModel) {
        // ---------------------------------------------------------------------
        // Principal -> wahlbezirksart
        // ---------------------------------------------------------------------
        WahlbezirkArt wahlbezirkArt = getWahlbezirkArt();

        // ---------------------------------------------------------------------
        // BasisdatenService -> zuerstAuszuzählendeWahl
        // ---------------------------------------------------------------------
        // todo: datentyp klären
        List<WahlModel> wahlen = wahlenClient.getWahlen(wahltagModel.wahltagID());
        if (wahlen == null) throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.BASISDATEN_ANTWORT_NULL);
        Wahlart zuerstAuszuzaehlendeWahl = wahlen.get(0).wahlart();

        List<Wahlvorstandsmitglied> collect = wahlvorstand.getWahlvorstandsmitglieder().stream()
                .map(mitglied -> populateWahlvorstandsmitgliedFunktionsnameOnline(mitglied, zuerstAuszuzaehlendeWahl, wahlbezirkArt))
                .toList();

        wahlvorstand.getWahlvorstandsmitglieder().clear();
        wahlvorstand.getWahlvorstandsmitglieder().addAll(collect);
        return wahlvorstand;
    }

    private Wahlvorstandsmitglied populateWahlvorstandsmitgliedFunktionsnameOnline(Wahlvorstandsmitglied
            mitglied, Wahlart wahlart, WahlbezirkArt wahlbezirkArt) {
        StringBuilder funktionsBuilder = new StringBuilder();
        String thisFunktion = "";

        if (wahlbezirkArt.equals(WahlbezirkArt.UWB)) {
            Map<String, Map<String, String>> uwbMappings = uwbNamenMapping.getUwbFunktion();
            if (uwbMappings != null) {
                Map<String, String> wahlartMapping = uwbMappings.get(wahlart.name());
                if (wahlartMapping != null) {
                    thisFunktion = wahlartMapping.get(mitglied.getFunktion().name());
                }
            }
        } else if (wahlbezirkArt.equals(WahlbezirkArt.BWB)) {
            Map<String, Map<String, String>> bwbMappings = bwbNamenMapping.getBwbFunktion();
            if (bwbMappings != null) {
                Map<String, String> wahlartMapping = bwbMappings.get(wahlart.name());
                if (wahlartMapping != null) {
                    thisFunktion = wahlartMapping.get(mitglied.getFunktion().name());
                }
            }
        }

        if (thisFunktion == null || thisFunktion.isEmpty()) {
            funktionsBuilder.append(mitglied.getFunktion());
        } else {
            funktionsBuilder.append(thisFunktion);
        }

        mitglied.setFunktionsName(funktionsBuilder.toString());
        return mitglied;
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

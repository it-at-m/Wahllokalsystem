package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.Wahlvorstand;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.WahlvorstandRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.authentication.AuthDetailRetriever;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
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
  private final FunktionsnamenMappingProperties namenMapping;
  private final Collection<AuthDetailRetriever> authDetailRetrivers;

  private static final WahlbezirkArtModel WAHLBEZIRK_ART_FALLBACK = WahlbezirkArtModel.UWB;
  private static final String FALLBACK_STRING = "FALLBACK_";

  @PreAuthorize(
      "hasAuthority('Wahlvorstand_BUSINESSACTION_GetWahlvorstand')"
          + "and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)")
  public Optional<WahlvorstandModel> getWahlvorstand(@P("wahlbezirkID") final String wahlbezirkID) {
    log.info("#getWahlvorstand");
    wahlvorstandValidator.validWahlbezirkIDOrThrow(wahlbezirkID);
    return wahlvorstandRepository.findById(wahlbezirkID).map(wahlvorstandModelMapper::toModel);
  }

  @PreAuthorize("hasAuthority('Wahlvorstand_BUSINESSACTION_UpdateWahlvorstand')")
  public Optional<WahlvorstandModel> updateWahlvorstand(
      @P("wahlbezirkID") final String wahlbezirkID) {
    log.info("#updateWahlvorstand");
    wahlvorstandValidator.validWahlbezirkIDOrThrow(wahlbezirkID);
    val konfigurierterWahltagModel = konfigurierterWahltagClient.getKonfigurierterWahltag();
    val wahlvorstand =
        wahlvorstandEaiClient.getWahlvorstand(wahlbezirkID, konfigurierterWahltagModel.wahltag());
    return Optional.ofNullable(persistWahlvorstand(wahlvorstand, konfigurierterWahltagModel));
  }

  @Transactional
  @PreAuthorize("hasAuthority('Wahlvorstand_BUSINESSACTION_PostWahlvorstand')")
  public void postWahlvorstand(@P("param") WahlvorstandModel wahlvorstandModel) {
    log.info("#postWahlvorstand");
    wahlvorstandValidator.validWahlvorstandOrThrow(wahlvorstandModel);

    if (wahlvorstandModel
        .wahlvorstandsmitglieder()
        .get(0)
        .identifikator()
        .startsWith(FALLBACK_STRING)) {
      log.info("Fallback-Daten vorhanden. Wahlvorstand wird nicht gespeichert.");
    } else {
      try {
        wahlvorstandRepository.save(wahlvorstandModelMapper.toEntity(wahlvorstandModel));
      } catch (Exception e) {
        log.error("#postWahlvorstand unsaveable: ", e);
        throw exceptionFactory.createTechnischeWlsException(
            ExceptionConstants.POSTWAHLVORSTAND_NOT_SAVEABLE);
      }
      wahlvorstandEaiClient.postWahlvorstand(wahlvorstandModel);
    }
  }

  @PreAuthorize("hasAuthority('Wahlvorstand_BUSINESSACTION_UpdateWahlvorstand')")
  public Optional<WahlvorstandModel> getFallbackWahlvorstand(String wahlbezirkID) {
    val fallbackWahlvorstand =
        WahlvorstandModel.builder()
            .wahlbezirkID(wahlbezirkID)
            .wahlvorstandsmitglieder(new ArrayList<>())
            .build();

    Arrays.stream(FunktionModel.values())
        .forEach(
            funktion -> {
              WahlvorstandsmitgliedModel mitglied =
                  WahlvorstandsmitgliedModel.builder()
                      .identifikator(FALLBACK_STRING + funktion + wahlbezirkID)
                      .funktion(funktion)
                      .familienname("______________")
                      .vorname("______________")
                      .build();
              fallbackWahlvorstand.wahlvorstandsmitglieder().add(mitglied);
            });

    val konfigurierterWahltagModel = konfigurierterWahltagClient.getKonfigurierterWahltag();
    return Optional.of(persistWahlvorstand(fallbackWahlvorstand, konfigurierterWahltagModel));
  }

  private WahlvorstandModel persistWahlvorstand(
      WahlvorstandModel wahlvorstand, KonfigurierterWahltagModel konfigurierterWahltagModel) {
    if (wahlvorstand == null || CollectionUtils.isEmpty(wahlvorstand.wahlvorstandsmitglieder())) {
      return null;
    }

    val wahlvorstandDB = wahlvorstandRepository.findById(wahlvorstand.wahlbezirkID());
    if (wahlvorstandDB.isPresent()) {
      try {
        val wahlvorstandMitFunktionsnamen =
            populateFunktionsnameOffline(wahlvorstand, wahlvorstandDB.get());
        wahlvorstandRepository.save(
            wahlvorstandModelMapper.toEntity(wahlvorstandMitFunktionsnamen));
        return wahlvorstandMitFunktionsnamen;
      } catch (IllegalStateException ex) {
        val wahlvorstandMitFunktionsnamen =
            populateFunktionsnameOnline(wahlvorstand, konfigurierterWahltagModel);
        wahlvorstandRepository.save(
            wahlvorstandModelMapper.toEntity(wahlvorstandMitFunktionsnamen));
        return wahlvorstandMitFunktionsnamen;
      }
    } else {
      val wahlvorstandMitFunktionsnamen =
          populateFunktionsnameOnline(wahlvorstand, konfigurierterWahltagModel);
      wahlvorstandRepository.save(wahlvorstandModelMapper.toEntity(wahlvorstandMitFunktionsnamen));
      return wahlvorstandMitFunktionsnamen;
    }
  }

  private WahlvorstandModel populateFunktionsnameOffline(
      WahlvorstandModel wahlvorstand, Wahlvorstand wahlvorstandDB) throws IllegalStateException {
    val collect =
        wahlvorstand.wahlvorstandsmitglieder().stream()
            .map(
                mitglied ->
                    populateWahlvorstandsmitgliedFunktionsnameOffline(mitglied, wahlvorstandDB))
            .toList();
    wahlvorstand.wahlvorstandsmitglieder().clear();
    wahlvorstand.wahlvorstandsmitglieder().addAll(collect);
    return wahlvorstand;
  }

  private WahlvorstandsmitgliedModel populateWahlvorstandsmitgliedFunktionsnameOffline(
      WahlvorstandsmitgliedModel mitglied, Wahlvorstand wahlvorstandDB)
      throws IllegalStateException {
    val functionOfMitglied = wahlvorstandModelMapper.toEntity(mitglied.funktion());
    val mitgliedDB =
        wahlvorstandDB.getWahlvorstandsmitglieder().stream()
            .filter(
                wahlvorstandsmitglied ->
                    wahlvorstandsmitglied.getFunktion().equals(functionOfMitglied))
            .findFirst();
    return mitgliedDB
        .map(
            m ->
                new WahlvorstandsmitgliedModel(
                    mitglied.identifikator(),
                    mitglied.familienname(),
                    mitglied.vorname(),
                    mitglied.funktion(),
                    m.getFunktionsname(),
                    mitglied.anwesend()))
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "Bisher unbekannte Funktion in Wahlvorstand gefunden. Muss neu gemappt werden."));
  }

  private WahlvorstandModel populateFunktionsnameOnline(
      WahlvorstandModel wahlvorstand, KonfigurierterWahltagModel wahltagModel) {
    val wahlbezirkArt = getWahlbezirkArtOfAuthenticaton();

    val wahlen = wahlenClient.getWahlen(wahltagModel);
    if (wahlen == null)
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.BASISDATEN_ANTWORT_NULL);
    val zuerstAuszuzaehlendeWahlArt = wahlen.get(0).wahlart();

    val collect =
        wahlvorstand.wahlvorstandsmitglieder().stream()
            .map(
                mitglied ->
                    populateWahlvorstandsmitgliedFunktionsnameOnline(
                        mitglied, zuerstAuszuzaehlendeWahlArt, wahlbezirkArt))
            .toList();
    wahlvorstand.wahlvorstandsmitglieder().clear();
    wahlvorstand.wahlvorstandsmitglieder().addAll(collect);
    return wahlvorstand;
  }

  private WahlvorstandsmitgliedModel populateWahlvorstandsmitgliedFunktionsnameOnline(
      WahlvorstandsmitgliedModel mitglied, WahlartModel wahlart, WahlbezirkArtModel wahlbezirkArt) {
    val funktionsBuilder = new StringBuilder();
    val thisFunktion = getFunktion(wahlbezirkArt, mitglied, wahlart);
    if (thisFunktion == null || thisFunktion.isEmpty()) {
      funktionsBuilder.append(mitglied.funktion());
    } else {
      funktionsBuilder.append(thisFunktion);
    }
    return new WahlvorstandsmitgliedModel(
        mitglied.identifikator(),
        mitglied.familienname(),
        mitglied.vorname(),
        mitglied.funktion(),
        funktionsBuilder.toString(),
        mitglied.anwesend());
  }

  private String getFunktion(
      WahlbezirkArtModel wahlbezirkArt, WahlvorstandsmitgliedModel mitglied, WahlartModel wahlart) {
    String funktion = "";
    val mappings = namenMapping.getMapping().get(wahlbezirkArt);

    if (mappings != null) {
      Map<String, String> wahlartMapping = mappings.get(wahlart.name());
      if (wahlartMapping != null) {
        funktion = wahlartMapping.get(mitglied.funktion().name());
      }
    }
    return funktion;
  }

  private WahlbezirkArtModel getWahlbezirkArtOfAuthenticaton() {
    val currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
    val authDetailRetriever =
        authDetailRetrivers.stream()
            .filter(retriever -> retriever.canHandle(currentAuthentication))
            .findFirst();
    if (authDetailRetriever.isPresent()) {
      val wahlbezirkOfUser =
          authDetailRetriever.get().getDetail("wahlbezirksArt", currentAuthentication);
      return wahlbezirkOfUser
          .map(WahlbezirkArtModel::valueOf)
          .orElseGet(
              () -> {
                log.error(
                    "#getKonfiguration Error: Wahlbezirkart konnte nicht erkannt werden. UWB wurde als Standardwert angenommen");
                return WAHLBEZIRK_ART_FALLBACK;
              });
    } else {
      log.error(
          "kein handler für authentication class {} vorhanden. Verwende Wahlbezirksart-Fallback {}",
          currentAuthentication.getClass(),
          WAHLBEZIRK_ART_FALLBACK);
      return WAHLBEZIRK_ART_FALLBACK;
    }
  }
}

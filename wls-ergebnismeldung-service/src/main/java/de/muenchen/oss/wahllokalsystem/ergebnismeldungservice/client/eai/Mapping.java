package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.eai;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerte;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.AWerteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnisDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.UngueltigeStimmzettelDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Mapping {

  public AWerteDTO toClientDTO(final AWerte aWerte) {
    AWerteDTO aoueaiAWerte = new AWerteDTO();
    if (aWerte != null) {
      aoueaiAWerte.setA1(aWerte.getA1());
      aoueaiAWerte.setA2(aWerte.getA2());
    }
    return aoueaiAWerte;
  }

  public Set<ErgebnisDTO> toDtoErgebnisseSet(final Collection<ErgebnisseModel> ergebnisse) {
    Set<ErgebnisDTO> ergebnisSet = new HashSet<>();

    ergebnisse.forEach(
        ergebnisList -> {
          val stapelart = ergebnisList.stapelart();
          ergebnisList
              .ergebnisse()
              .forEach(
                  ergebnis -> {
                    ErgebnisDTO aoueaiErgebnis = new ErgebnisDTO();
                    aoueaiErgebnis.setErgebnis(ergebnis.ergebnis());
                    aoueaiErgebnis.setKandidatID(ergebnis.kandidatID());
                    aoueaiErgebnis.setWahlvorschlagID(ergebnis.wahlvorschlagID());

                    val wahlvorschlagsordnungszahl = ergebnis.wahlvorschlagsordnungszahl();
                    if (wahlvorschlagsordnungszahl == null) {
                      log.warn(
                          "toAoueaiErgebnisseSet 4.1.1  fehler - wahlvorschlagsordnungszahl is null");
                      log.warn("toAoueaiErgebnisseSet 4.1.1.1  ergebnisse: {} ", ergebnisse);
                      log.warn("toAoueaiErgebnisseSet 4.1.1.2  ergebnisList: {} ", ergebnisList);
                      log.warn("toAoueaiErgebnisseSet 4.1.1.3  ergebnis: {} ", ergebnis);
                    }
                    aoueaiErgebnis.setWahlvorschlagsordnungszahl(wahlvorschlagsordnungszahl);

                    aoueaiErgebnis.setStimmenart(stapelart.name());
                    ergebnisSet.add(aoueaiErgebnis);
                  });
        });

    return ergebnisSet;
  }

  public ErgebnismeldungDTO.WahlartEnum toWahlartDTO(final WahlartModel wahlart) {
    try {
      return ErgebnismeldungDTO.WahlartEnum.valueOf(wahlart.name());
    } catch (Exception e) {
      log.error("#convertWahlart: parsing Exception", e);
    }
    return null;
  }

  public Set<UngueltigeStimmzettelDTO> toDtoSet(
      final Collection<ErgebnisseModel> ungueltigeErgebnisse) {
    Set<UngueltigeStimmzettelDTO> ungueltigeStimmzettelSet = new HashSet<>();
    ungueltigeErgebnisse.forEach(
        ungueltigesErgebnis -> {
          val stapelart = ungueltigesErgebnis.stapelart();
          ungueltigesErgebnis
              .ergebnisse()
              .forEach(ergebnis -> ungueltigeStimmzettelSet.add(toDto(ergebnis, stapelart)));
        });
    return ungueltigeStimmzettelSet;
  }

  public ErgebnismeldungDTO.MeldungsartEnum toDTO(final MeldungsartModel meldungsart) {
    return switch (meldungsart) {
      case V1 -> ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT;
      case V3 -> ErgebnismeldungDTO.MeldungsartEnum.SCHNELLMELDUNG;
    };
  }

  private UngueltigeStimmzettelDTO toDto(
      final ErgebnisModel ergebnis, final StapelartModel stapelart) {
    UngueltigeStimmzettelDTO ungueltigeStimmzettel = new UngueltigeStimmzettelDTO();
    ungueltigeStimmzettel.setWahlvorschlagID(ergebnis.wahlvorschlagID());
    ungueltigeStimmzettel.setAnzahl(ergebnis.ergebnis());
    ungueltigeStimmzettel.setStimmenart(stapelart.name());
    return ungueltigeStimmzettel;
  }
}

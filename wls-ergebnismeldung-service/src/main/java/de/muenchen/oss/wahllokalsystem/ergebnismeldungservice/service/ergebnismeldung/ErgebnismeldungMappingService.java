package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.eai.Mapping;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.BezirkUndWahlIDUndWaehlerverzeichnisnummer;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.EingenommenerWahlschein;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.StimmabgabevermerkeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.StimmabgabevermerkeStimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.Stimmzettelumschlaege;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.StimmzettelumschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.AWerteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.BWerteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.UngueltigeStimmzettelDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.WahlbriefeWerteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.authentication.AuthenticationService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.MBWBedenklicheStimmzettelService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErgebnismeldungMappingService {

  private final StimmzettelumschlaegeRepository stimmzettelumschlaegeRepo;
  private final StimmabgabevermerkeRepository stimmabgabevermerkeRepo;
  private final AWerteRepository aWerteRepo;
  private final MBWBedenklicheStimmzettelService bedenklicheStimmzettelService;

  private final AuthenticationService authenticationService;
  private final BriefwahlClient briefwahlClient;

  private final List<ErgebnismeldungsErgebnisseMapper> ergebnismeldungsErgebnisseMapper;

  private final Mapping mapping;

  public ErgebnismeldungDTO createErgebnismeldung(
      final WahlartModel wahlart,
      final String wahlID,
      final String wahlbezirkID,
      final Long waehlerverzeichnisNummer,
      final MeldungsartModel meldungsart,
      final String hauptwahlbezirkID) {
    val ergebnismeldungsMapper = ergebnismeldungsErgebnisseMapper.stream()
        .filter(erm -> erm.canHandleWahlart(wahlart))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No mapper found for wahlart " + wahlart));


    val ergebnismeldung = new ErgebnismeldungDTO();
    ergebnismeldung.setWahlID(wahlID);
    ergebnismeldung.setWahlbezirkID(wahlbezirkID);

    val bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);
    val bezirkIDUndWaehlerverzeichnisNummer =
        new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer);

    val wahlbezirkArtOfUser =
        authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow();
    ergebnismeldung.setaWerte(getAWerte(wahlbezirkArtOfUser, bezirkUndWahlID));

    val bWerte =
        getBWerte(
            wahlbezirkArtOfUser, bezirkUndWahlID, bezirkIDUndWaehlerverzeichnisNummer, wahlart);
    ergebnismeldung.setbWerte(bWerte);

    log.debug(
        "SENDERGEBNISSE BUSINESSAKTION #sendergebnis 3.2  a createErgebnismeldung wahlart {}",
        wahlart);

    val ergebnismeldungErgebnisse =
            ergebnismeldungsMapper.getErgebnismeldungErgebnisse(
            wahlID, wahlbezirkID, wahlart, meldungsart);
    ergebnismeldung.setErgebnisse(
        mapping.toDtoErgebnisseSet(ergebnismeldungErgebnisse.gueltigeErgebnisse()));
    val eaiMeldungsart = mapping.toDTO(meldungsart);
    ergebnismeldung.meldungsart(eaiMeldungsart);
    log.debug("SENDERGEBNISSE BUSINESSAKTION #sendergebnis 3.2  b createErgebnismeldung");

    ergebnismeldung.setUngueltigeStimmzettels(
        mapping.toDtoSet(ergebnismeldungErgebnisse.ungueltigeErgebnisse()));
    ergebnismeldung.setUngueltigeStimmzettelAnzahl(
        (long) ergebnismeldungErgebnisse.ungueltigeErgebnisse().size());
    ergebnismeldung.setWahlart(mapping.toWahlartDTO(wahlart));

    if (ergebnismeldung.getUngueltigeStimmzettels() == null) {
      ergebnismeldung.setUngueltigeStimmzettels(
          Set.of(getUngueltigeBedenklicheStimmzettel(wahlID, wahlbezirkID)));
    } else {
      ergebnismeldung
          .getUngueltigeStimmzettels()
          .add(getUngueltigeBedenklicheStimmzettel(wahlID, wahlbezirkID));
    }

    log.debug(
        "SENDERGEBNISSE BUSINESSAKTION #sendergebnis 3.2  c createErgebmismeldung hauptwahlbezirkID {}",
        hauptwahlbezirkID);
    if (wahlbezirkArtOfUser == WahlbezirkArtModel.BWB
        && eaiMeldungsart.equals(ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT)) {
      long zurueckgewiesenGesamt =
          briefwahlClient.getAnzahlZurueckgewiesenerWahlbriefe(
              hauptwahlbezirkID, wahlID, waehlerverzeichnisNummer);
      val wbw = new WahlbriefeWerteDTO();
      wbw.setZurueckgewiesenGesamt(zurueckgewiesenGesamt);
      ergebnismeldung.setWahlbriefeWerte(wbw);
    }

    return ergebnismeldung;
  }

  private AWerteDTO getAWerte(
      final WahlbezirkArtModel wahlbezirkArt, final BezirkUndWahlID bezirkUndWahlID) {
    if (wahlbezirkArt == WahlbezirkArtModel.UWB) {
      val aWerte = aWerteRepo.findById(bezirkUndWahlID).orElse(null);
      return mapping.toClientDTO(aWerte);
    } else {
      return null;
    }
  }

  private BWerteDTO getBWerte(
      final WahlbezirkArtModel wahlbezirkArt,
      final BezirkUndWahlID bezirkUndWahlID,
      final BezirkIDUndWaehlerverzeichnisNummer bezirkIDUndWaehlerverzeichnisNummer,
      final WahlartModel wahlart) {
    return switch (wahlbezirkArt) {
      case UWB ->
          getBWerteDTOOfUWBWahlbezirk(
              bezirkUndWahlID.getWahlID(), bezirkIDUndWaehlerverzeichnisNummer, wahlart);
      case BWB -> getBWerteDTOOfBWBWahlbezirk(bezirkUndWahlID);
    };
  }

  private BWerteDTO getBWerteDTOOfUWBWahlbezirk(
      final String wahlID,
      final BezirkIDUndWaehlerverzeichnisNummer waehlerverzeichnisNummer,
      final WahlartModel wahlart) {
    val bWerte = new BWerteDTO();
    val wahldatenSet =
        stimmabgabevermerkeRepo
            .findById(
                new BezirkUndWahlIDUndWaehlerverzeichnisnummer(
                    waehlerverzeichnisNummer.getWahlbezirkID(),
                    wahlID,
                    waehlerverzeichnisNummer.getWaehlerverzeichnisNummer()))
            .orElseThrow(NullPointerException::new);
    val stimmzettelumschlaege =
        stimmzettelumschlaegeRepo
            .findById(new BezirkUndWahlID(wahlID, waehlerverzeichnisNummer.getWahlbezirkID()))
            .orElse(null);

    long eingenommeneWahlscheine =
        wahldatenSet.getEingenommeneWahlscheine().stream()
            .mapToLong(EingenommenerWahlschein::getAnzahl)
            .sum();
    bWerte.setB2(eingenommeneWahlscheine);

    long erfassteStimmabgabevermerke =
        wahldatenSet.getVermerke().stream()
            .mapToLong(
                vermerke ->
                    vermerke.getStimmzettel().stream()
                        .mapToLong(StimmabgabevermerkeStimmzettel::getAnzahl)
                        .sum())
            .sum();
    bWerte.setB1(erfassteStimmabgabevermerke);
    bWerte.setB(erfassteStimmabgabevermerke + eingenommeneWahlscheine);

    if ((wahlart.equals(WahlartModel.EUW)
            || wahlart.equals(WahlartModel.BTW)
            || wahlart.equals(WahlartModel.VE)
            || wahlart.equals(WahlartModel.BEB))
        && stimmzettelumschlaege != null) {
      bWerte.setB(stimmzettelumschlaege.getAnzahlWaehler());
    }
    return bWerte;
  }

  private BWerteDTO getBWerteDTOOfBWBWahlbezirk(final BezirkUndWahlID bezirkUndWahlID) {
    val bWerte = new BWerteDTO();

    Stimmzettelumschlaege stimmzettelumschlaege =
        stimmzettelumschlaegeRepo.findById(bezirkUndWahlID).orElseThrow(NullPointerException::new);
    bWerte.setB(stimmzettelumschlaege.getAnzahlWaehler());

    return bWerte;
  }

  private UngueltigeStimmzettelDTO getUngueltigeBedenklicheStimmzettel(
      final String wahlID, final String wahlbezirkID) {
    val ungueltigeStimmzettelDTO = new UngueltigeStimmzettelDTO();
    val anzahlUngueltigeBedenklicheStimmzettel =
        bedenklicheStimmzettelService.getAnzahlUngueltigeBedenklicheStimmzettel(
            new BezirkUndWahlID(wahlID, wahlbezirkID));

    ungueltigeStimmzettelDTO.setStimmenart("MBW_E_UNGUELTIG");
    ungueltigeStimmzettelDTO.setAnzahl(anzahlUngueltigeBedenklicheStimmzettel);

    return ungueltigeStimmzettelDTO;
  }
}

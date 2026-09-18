package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MBWStimmzettelErgebnisseMapper implements MBWStapelErgebnisCollector {

  private final StimmzettelService stimmzettelService;

  @Override
  @Transactional(readOnly = true)
  public MBWErgebnisseModel getErgebnisse(String wahlID, String wahlbezirkID) {
    val stapelA =
        stimmzettelService
            .getCountByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagSelected(
                new BezirkUndWahlID(wahlID, wahlbezirkID))
            .stream()
            .map(
                entry ->
                    new ErgebnisModel(entry.wahlvorschlagID(), null, null, entry.anzahl(), null))
            .toList();

    val stapelB =
        stimmzettelService
            .countByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagThatHasChanges(
                new BezirkUndWahlID(wahlID, wahlbezirkID))
            .stream()
            .map(
                entry ->
                    new ErgebnisModel(entry.wahlvorschlagID(), null, null, entry.anzahl(), null))
            .toList();

    val stapelBC =
        stimmzettelService.getKandidatVotes(new BezirkUndWahlID(wahlID, wahlbezirkID)).stream()
            .map(
                entry ->
                    new ErgebnisModel(
                        entry.wahlvorschlagID(), entry.kandidatID(), null, entry.anzahl(), null))
            .toList();

    val countStapelDUngueltig =
        List.of(
            new ErgebnisModel(
                null,
                null,
                null,
                stimmzettelService.getCountUngueltige(new BezirkUndWahlID(wahlID, wahlbezirkID)),
                null));

    return new MBWErgebnisseModel(stapelA, stapelB, countStapelDUngueltig, stapelBC);
  }
}

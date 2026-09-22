package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ergebnisseProvider;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.MBWStimmzettelService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MBWStimmzettelErgebnismeldungsErgebnisseProvider {

  private final MBWStimmzettelService mbwStimmzettelService;

  @Transactional(readOnly = true)
  public ErgebnismeldungsErgebnisseModel getErgebnisse(
      final String wahlID, final String wahlbezirkID, final MeldungsartModel meldungsartModel) {
    val gueltigeErgebnisse = new LinkedList<ErgebnisseModel>();

    val stapelA =
        mbwStimmzettelService
            .getCountByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagSelected(
                new BezirkUndWahlID(wahlID, wahlbezirkID))
            .stream()
            .map(
                entry ->
                    new ErgebnisModel(entry.wahlvorschlagID(), null, null, entry.anzahl(), null))
            .toList();
    gueltigeErgebnisse.add(
        new ErgebnisseModel(wahlbezirkID, wahlID, StapelartModel.MBW_A, stapelA));

    val stapelB =
        mbwStimmzettelService
            .countByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagThatHasChanges(
                new BezirkUndWahlID(wahlID, wahlbezirkID))
            .stream()
            .map(
                entry ->
                    new ErgebnisModel(entry.wahlvorschlagID(), null, null, entry.anzahl(), null))
            .toList();
    gueltigeErgebnisse.add(
        new ErgebnisseModel(wahlbezirkID, wahlID, StapelartModel.MBW_B, stapelB));

    if (!MeldungsartModel.V3.equals(meldungsartModel)) {
      val stapelBC =
          mbwStimmzettelService.getKandidatVotes(new BezirkUndWahlID(wahlID, wahlbezirkID)).stream()
              .map(
                  entry ->
                      new ErgebnisModel(
                          entry.wahlvorschlagID(), entry.kandidatID(), null, entry.anzahl(), null))
              .toList();
      gueltigeErgebnisse.add(
          new ErgebnisseModel(wahlbezirkID, wahlID, StapelartModel.MBW_B_C, stapelBC));
    }

    val countStapelDUngueltig =
        List.of(
            new ErgebnisModel(
                null,
                null,
                null,
                mbwStimmzettelService.getCountUngueltige(new BezirkUndWahlID(wahlID, wahlbezirkID)),
                null));
    val stapelDErgebnisseModel =
        new ErgebnisseModel(
            wahlbezirkID, wahlID, StapelartModel.MBW_D_UNGUELTIG, countStapelDUngueltig);

    return new ErgebnismeldungsErgebnisseModel(gueltigeErgebnisse, List.of(stapelDErgebnisseModel));
  }
}

package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.KandidatStimmenAnzahlModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.WahlvorschlagStimmzettelAnzahlModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.stream.Collectors;
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
        val stapelA = stimmzettelService.getStimmzettelWithExactlyOneWahlvorschlagSelected(new BezirkUndWahlID(wahlID, wahlbezirkID)).stream()
                .collect(Collectors.groupingBy(WahlvorschlagStimmzettelAnzahlModel::wahlvorschlagID, Collectors.summingLong(WahlvorschlagStimmzettelAnzahlModel::anzahl)));

        val stapelB = stimmzettelService.getStimmzettelWithExactlyOneWahlvorschlagSelectedAndNoCandidateVotes(new BezirkUndWahlID(wahlID, wahlbezirkID)).stream()
                .collect(Collectors.groupingBy(WahlvorschlagStimmzettelAnzahlModel::wahlvorschlagID, Collectors.summingLong(WahlvorschlagStimmzettelAnzahlModel::anzahl)));

        val stapelBC = stimmzettelService.getStapelBC(new BezirkUndWahlID(wahlID, wahlbezirkID)).stream()
                .collect(Collectors.groupingBy(
                        KandidatStimmenAnzahlModel::wahlvorschlagID,
                Collectors.groupingBy(
                        KandidatStimmenAnzahlModel::kandidatID,
                        Collectors.summingLong(KandidatStimmenAnzahlModel::anzahl))));

        val countStapelDUngueltig = stimmzettelService.getCountStapelDUngueltig(new BezirkUndWahlID(wahlID, wahlbezirkID));
        val countStapelEUngueltig = stimmzettelService.getCountStapelEUngueltig(new BezirkUndWahlID(wahlID, wahlbezirkID));

        return new MBWErgebnisseModel(stapelA, stapelB, countStapelDUngueltig, countStapelEUngueltig, stapelBC);
    }
}

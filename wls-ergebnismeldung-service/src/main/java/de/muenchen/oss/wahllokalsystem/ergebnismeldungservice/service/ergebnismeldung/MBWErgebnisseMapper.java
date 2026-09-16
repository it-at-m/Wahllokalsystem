package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MBWErgebnisseMapper implements ErgebnismeldungsErgebnisseMapper {

    private final MBWStimmzettelErgebnisseMapper mbwStimmzettelErgebnisseMapper;

    @Override
    public ErgebnismeldungsErgebnisseModel getErgebnismeldungErgebnisse(String wahlID, String wahlbezirkID, WahlartModel wahlart,
            MeldungsartModel meldungsart) {
        val mbwErgebnisse = mbwStimmzettelErgebnisseMapper.getErgebnisse(wahlID, wahlbezirkID);

        val stapelAErgebnisList = mbwErgebnisse.stapelA().entrySet().stream().map(entry -> new ErgebnisModel(entry.getKey(), null, null, entry.getValue(), null)).toList();
        val stapelAErgebnisse = new ErgebnisseModel(wahlbezirkID, wahlID, StapelartModel.MBW_A, stapelAErgebnisList);

        val stapelBErgebnisList = mbwErgebnisse.stapelB().entrySet().stream().map(entry -> new ErgebnisModel(entry.getKey(), null, null, entry.getValue(), null)).toList();
        val stapelBErgebnisse = new ErgebnisseModel(wahlbezirkID, wahlID, StapelartModel.MBW_B, stapelBErgebnisList);

        val stapelBCErgebnisList =  mbwErgebnisse.stimmenJeKandidatStapelBC().entrySet().stream().map(wahlvorschlagEntry ->
                wahlvorschlagEntry.getValue().entrySet().stream().map(kandidatEntry -> new ErgebnisModel(wahlvorschlagEntry.getKey(), kandidatEntry.getKey(),
                        null, kandidatEntry.getValue(), null)).toList()).flatMap(List::stream).toList();
        val stapelBCErgebnisse = new ErgebnisseModel(wahlbezirkID, wahlID, StapelartModel.MBW_B_C, stapelBCErgebnisList);

        val stapelDUngueltig = new ErgebnisModel(null, null, null,mbwErgebnisse.stapelDUngueltig(), null);
        val ergebnisseStapelDUngueltig = new ErgebnisseModel(wahlbezirkID, wahlID, StapelartModel.MBW_D_UNGUELTIG, List.of(stapelDUngueltig));

        val stapelEUngueltig = new ErgebnisModel(null, null, null, mbwErgebnisse.stapelEUngueltig(), null);
        val ergebnisseStapelEUngueltig = new ErgebnisseModel(wahlbezirkID, wahlID, StapelartModel.MBW_E_UNGUELTIG, List.of(stapelEUngueltig));

        return new ErgebnismeldungsErgebnisseModel(List.of(stapelAErgebnisse, stapelBErgebnisse, stapelBCErgebnisse), List.of(ergebnisseStapelDUngueltig, ergebnisseStapelEUngueltig));
    }

    @Override
    public boolean canHandleWahlart(WahlartModel wahlart) {
        return WahlartModel.MBW.equals(wahlart);
    }
}

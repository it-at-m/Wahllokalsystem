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

        mbwErgebnisse.stapelA().entrySet().stream().map(entry -> new ErgebnisModel(entry.getKey(), null, null, entry.getValue(), null)).toList();

        val stapelDUngueltig = mbwErgebnisse.stapelDUngueltig().entrySet().stream().map(entry -> new ErgebnisModel(entry.getKey(), null, null, entry.getValue(), null)).toList();
        val ergebnisseStapelDUngueltig = new ErgebnisseModel(wahlbezirkID, wahlID, StapelartModel.MBW_D_UNGUELTIG, stapelDUngueltig);

        val stapelEUngueltig = mbwErgebnisse.stapelEUngueltig().entrySet().stream().map(entry -> new ErgebnisModel(entry.getKey(), null, null, entry.getValue(), null)).toList();
        val ergebnisseStapelEUngueltig = new ErgebnisseModel(wahlbezirkID, wahlID, StapelartModel.MBW_E_UNGUELTIG, stapelEUngueltig);

        return new ErgebnismeldungsErgebnisseModel(null, List.of(ergebnisseStapelDUngueltig, ergebnisseStapelEUngueltig));
    }

    @Override
    public boolean canHandleWahlart(WahlartModel wahlart) {
        return WahlartModel.MBW.equals(wahlart);
    }
}

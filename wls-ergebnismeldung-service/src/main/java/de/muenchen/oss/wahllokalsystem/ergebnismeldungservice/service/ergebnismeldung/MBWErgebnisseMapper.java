package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
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
  public ErgebnismeldungsErgebnisseModel getErgebnismeldungErgebnisse(
      String wahlID, String wahlbezirkID, WahlartModel wahlart, MeldungsartModel meldungsart) {
    val mbwErgebnisse = mbwStimmzettelErgebnisseMapper.getErgebnisse(wahlID, wahlbezirkID);

    val stapelAErgebnisse = new ErgebnisseModel(wahlbezirkID, wahlID, StapelartModel.MBW_A, mbwErgebnisse.stapelA());
    val stapelBErgebnisse =
        new ErgebnisseModel(wahlbezirkID, wahlID, StapelartModel.MBW_B, mbwErgebnisse.stapelB());
    val stapelBCErgebnisse =
        new ErgebnisseModel(wahlbezirkID, wahlID, StapelartModel.MBW_B_C, mbwErgebnisse.stimmenJeKandidatStapelBC());
    val ergebnisseStapelDUngueltig =
        new ErgebnisseModel(
            wahlbezirkID, wahlID, StapelartModel.MBW_D_UNGUELTIG, mbwErgebnisse.stapelDUngueltig());

    return new ErgebnismeldungsErgebnisseModel(
        List.of(stapelAErgebnisse, stapelBErgebnisse, stapelBCErgebnisse),
        List.of(ergebnisseStapelDUngueltig));
  }

  @Override
  public boolean canHandleWahlart(WahlartModel wahlart) {
    return WahlartModel.MBW.equals(wahlart);
  }
}

package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MBWErgebnisseMapper implements ErgebnismeldungsErgebnisseMapper {

  private final MBWStimmzettelErgebnisseMapper mbwStimmzettelErgebnisseMapper;

  private final MBWStapelErgebnisseMapper mbwStapelErgebnisseMapper;

  @Override
  public ErgebnismeldungsErgebnisseModel getErgebnismeldungErgebnisse(
      String wahlID, String wahlbezirkID, WahlartModel wahlart, MeldungsartModel meldungsart) {
    val stimmzettelErgebnisse = mbwStimmzettelErgebnisseMapper.getErgebnisse(wahlID, wahlbezirkID);
    val stapelErgebnisse = mbwStapelErgebnisseMapper.getErgebnisse(wahlID, wahlbezirkID);

    return new ErgebnismeldungsErgebnisseModel(
        Stream.concat(
                stimmzettelErgebnisse.gueltigeErgebnisse().stream(),
                stapelErgebnisse.gueltigeErgebnisse().stream())
            .toList(),
        Stream.concat(
                stimmzettelErgebnisse.ungueltigeErgebnisse().stream(),
                stapelErgebnisse.ungueltigeErgebnisse().stream())
            .toList());
  }

  @Override
  public boolean canHandleWahlart(WahlartModel wahlart) {
    return WahlartModel.MBW.equals(wahlart);
  }
}

package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ergebnisseProvider;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MBWErgebnisseProvider implements ErgebnismeldungsErgebnisseProvider {

  private final MBWStimmzettelErgebnismeldungsErgebnisseProvider
      mbwStimmzettelErgebnismeldungsErgebnisseProvider;

  private final MBWStapelErgebnismeldungsErgebnisseProvider
      mbwStapelErgebnismeldungsErgebnisseProvider;

  @Override
  public ErgebnismeldungsErgebnisseModel getErgebnismeldungErgebnisse(
      final String wahlID,
      final String wahlbezirkID,
      final WahlartModel wahlart,
      final MeldungsartModel meldungsart) {
    val stimmzettelErgebnisse =
        mbwStimmzettelErgebnismeldungsErgebnisseProvider.getErgebnisse(
            wahlID, wahlbezirkID, meldungsart);
    val stapelErgebnisse =
        mbwStapelErgebnismeldungsErgebnisseProvider.getErgebnisse(wahlID, wahlbezirkID);

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
  public boolean canHandleWahlart(final WahlartModel wahlart) {
    return WahlartModel.MBW.equals(wahlart);
  }
}

package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ergebnisseProvider;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErgebnismeldungErgebnisseService {

  private final List<ErgebnismeldungsErgebnisseProvider> ergebnisseMappers;

  public ErgebnismeldungsErgebnisseModel getErgebnisse(
      String wahlID, String wahlbezirkID, WahlartModel wahlart, MeldungsartModel meldungsart) {
    val ergebnismeldungsMapper =
        ergebnisseMappers.stream()
            .filter(erm -> erm.canHandleWahlart(wahlart))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("No mapper found for wahlart " + wahlart));

    return ergebnismeldungsMapper.getErgebnismeldungErgebnisse(
        wahlID, wahlbezirkID, wahlart, meldungsart);
  }
}

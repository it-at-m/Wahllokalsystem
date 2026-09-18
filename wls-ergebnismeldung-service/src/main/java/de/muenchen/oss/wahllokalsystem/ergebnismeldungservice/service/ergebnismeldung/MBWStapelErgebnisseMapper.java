package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.WahlartPredicateHolder;
import java.util.Collection;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MBWStapelErgebnisseMapper {

  private final ErgebnisseService ergebnisseService;

  private final WahlartPredicateHolder wahlartPredicateHolder;

  public ErgebnismeldungsErgebnisseModel getErgebnisse(String wahlID, String wahlbezirkID) {
    val ergebnisse = ergebnisseService.getAllErgebnisse(wahlID, wahlbezirkID);

    val gueltigeErgebnisse = getErgebnisse(ergebnisse, true);
    val ungueltigeErgebnisse = getErgebnisse(ergebnisse, false);

    return new ErgebnismeldungsErgebnisseModel(gueltigeErgebnisse, ungueltigeErgebnisse);
  }

  private Collection<ErgebnisseModel> getErgebnisse(
      final Collection<ErgebnisseModel> ergebnisse, final boolean gueltig) {
    val predicateForStapelWithInvalidErgebnisse =
        wahlartPredicateHolder.getPredicateForStapelWithInvalidErgebnisse(WahlartModel.MBW);
    val ergebnisseFilter =
        gueltig
            ? Predicate.not(predicateForStapelWithInvalidErgebnisse)
            : predicateForStapelWithInvalidErgebnisse;

    return ergebnisse.stream()
        .filter(ergebnis -> ergebnisseFilter.test(ergebnis.stapelart()))
        .toList();
  }
}

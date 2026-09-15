package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
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
public class DefaultStapelErgebnisseMapper implements ErgebnismeldungsErgebnisseMapper {

  private final ErgebnisseService ergebnisseService;

  private final WahlartPredicateHolder wahlartPredicateHolder;

  @Override
  public ErgebnismeldungsErgebnisseModel getErgebnismeldungErgebnisse(
      String wahlID, String wahlbezirkID, WahlartModel wahlart, MeldungsartModel meldungsart) {
    val ergebnisse = ergebnisseService.getAllErgebnisse(wahlID, wahlbezirkID);

    val gueltigeErgebnisse = getErgebnisse(wahlart, ergebnisse, true);
    val ungueltigeErgebnisse = getErgebnisse(wahlart, ergebnisse, false);

    return new ErgebnismeldungsErgebnisseModel(gueltigeErgebnisse, ungueltigeErgebnisse);
  }

  @Override
  public boolean canHandleWahlart(WahlartModel wahlart) {
    return true;
  }

  private Collection<ErgebnisseModel> getErgebnisse(
      final WahlartModel wahlart,
      final Collection<ErgebnisseModel> ergebnisse,
      final boolean gueltig) {
    val predicateForStapelWithInvalidErgebnisse =
        wahlartPredicateHolder.getPredicateForStapelWithInvalidErgebnisse(wahlart);
    val ergebnisseFilter =
        gueltig
            ? Predicate.not(predicateForStapelWithInvalidErgebnisse)
            : predicateForStapelWithInvalidErgebnisse;

    return ergebnisse.stream()
        .filter(ergebnis -> ergebnisseFilter.test(ergebnis.stapelart()))
        .toList();
  }
}

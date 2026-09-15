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
  public Collection<ErgebnisseModel> getGueltigeErgebnisse(
      final String wahlID,
      final String wahlbezirkID,
      final WahlartModel wahlart,
      final MeldungsartModel meldungsart) {
    val ergebnisse = ergebnisseService.getAllErgebnisse(wahlID, wahlbezirkID);

    return getErgebnisse(wahlart, ergebnisse, true);
  }

  @Override
  public Collection<ErgebnisseModel> getUngueltigeErgebnisse(
      final String wahlID,
      final String wahlbezirkID,
      final WahlartModel wahlart,
      final MeldungsartModel meldungsart) {
    val ergebnisse = ergebnisseService.getAllErgebnisse(wahlID, wahlbezirkID);

    return getErgebnisse(wahlart, ergebnisse, false);
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

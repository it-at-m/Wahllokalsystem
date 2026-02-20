package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class WahlartPredicateHolder {

  private final Map<WahlartModel, InvalidErgebnissePredicateModel> predicatesForWahlarten =
      Map.ofEntries(
          Map.entry(
              WahlartModel.BAW,
              new InvalidErgebnissePredicateModel(List.of(StapelartModel.SRW_BAW_D_UNGUELTIG))),
          Map.entry(
              WahlartModel.BEB,
              new InvalidErgebnissePredicateModel(
                  List.of(StapelartModel.VE_BE_D_UNGUELTIG, StapelartModel.VE_BE_CBA_UNGUELTIG))),
          Map.entry(
              WahlartModel.BTW,
              new InvalidErgebnissePredicateModel(
                  List.of(
                      StapelartModel.BTW_B_II_UNGUELTIG,
                      StapelartModel.BTW_B_I_UNGUELTIG,
                      StapelartModel.BTW_C_UNGEKENNZEICHNET,
                      StapelartModel.BTW_C_LEER,
                      StapelartModel.BTW_D_I_UNGUELTIG,
                      StapelartModel.BTW_D_II_UNGUELTIG))),
          Map.entry(
              WahlartModel.BZW,
              new InvalidErgebnissePredicateModel(
                  List.of(
                      StapelartModel.LTW_BZW_C_UNGUELTIG,
                      StapelartModel.LTW_BZW_F_UNGUELTIG,
                      StapelartModel.LTW_BZW_B,
                      StapelartModel.LTW_BZW_E,
                      StapelartModel.LTW_BZW_G_BEIDE,
                      StapelartModel.LTW_BZW_G_GROSS,
                      StapelartModel.LTW_BZW_G_KLEIN))),
          Map.entry(
              WahlartModel.EUW,
              new InvalidErgebnissePredicateModel(
                  List.of(
                      StapelartModel.EUW_C_UNGUELTIG,
                      StapelartModel.EUW_B_LEER,
                      StapelartModel.EUW_B_UNGEKENNZEICHNET))),
          Map.entry(
              WahlartModel.LTW,
              new InvalidErgebnissePredicateModel(
                  List.of(
                      StapelartModel.LTW_BZW_C_UNGUELTIG,
                      StapelartModel.LTW_BZW_F_UNGUELTIG,
                      StapelartModel.LTW_BZW_B,
                      StapelartModel.LTW_BZW_E,
                      StapelartModel.LTW_BZW_G_BEIDE,
                      StapelartModel.LTW_BZW_G_GROSS,
                      StapelartModel.LTW_BZW_G_KLEIN))),
          Map.entry(
              WahlartModel.MBW,
              new InvalidErgebnissePredicateModel(List.of(StapelartModel.MBW_D_UNGUELTIG))),
          Map.entry(
              WahlartModel.OBW,
              new InvalidErgebnissePredicateModel(
                  List.of(
                      StapelartModel.OBW_C_UNGUELTIG,
                      StapelartModel.OBW_B_LEER,
                      StapelartModel.OBW_B_UNGEKENNZEICHNET))),
          Map.entry(
              WahlartModel.SRW,
              new InvalidErgebnissePredicateModel(List.of(StapelartModel.SRW_BAW_D_UNGUELTIG))),
          Map.entry(
              WahlartModel.VE,
              new InvalidErgebnissePredicateModel(List.of(StapelartModel.VE_BE_D_UNGUELTIG))));

  public Predicate<StapelartModel> getPredicateForStapelWithInvalidErgebnisse(
      final WahlartModel wahlart) {
    if (!predicatesForWahlarten.containsKey(wahlart)) {
      throw new IllegalArgumentException("Wahlart " + wahlart + " wird nicht unterstützt");
    }

    return predicatesForWahlarten.get(wahlart);
  }

  private record InvalidErgebnissePredicateModel(
      Collection<StapelartModel> stapelRepresentingInvalid) implements Predicate<StapelartModel> {

    @Override
    public boolean test(final StapelartModel stapelart) {
      return stapelRepresentingInvalid.contains(stapelart);
    }
    // CHECKSTYLE.OFF: WhitespaceAround: WhitespaceAround: '}' is not followed by whitespace. Empty
    // blocks may only be represented as {} when not part of a multi-block statement (4.1.3)
  }
}
// CHECKSTYLE.ON: AbbreviationAsWordInName

package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ergebnisseProvider;

import static org.instancio.Select.field;
import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.WahlartPredicateHolder;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MBWStapelErgebnismeldungsErgebnisseProviderTest {

  private static final String WAHL_ID = Instancio.create(String.class);
  private static final String WAHLBEZIRK_ID = Instancio.create(String.class);

  @Mock ErgebnisseService ergebnisseService;

  @Mock WahlartPredicateHolder wahlartPredicateHolder;

  @InjectMocks MBWStapelErgebnismeldungsErgebnisseProvider unitUnderTest;

  @Nested
  class GetErgebnisse {

    @Test
    void should_returnGueltigeAndUngueltigeErgebnisse_when_ergebnisseAreGiven() {
      val gueltigesErgebnisA = ergebnisWithStapelart(StapelartModel.MBW_A);
      val ungueltigesErgebnisD = ergebnisWithStapelart(StapelartModel.MBW_D_UNGUELTIG);
      val gueltigesErgebnisBC = ergebnisWithStapelart(StapelartModel.MBW_B_C);
      val ergebnisse = List.of(gueltigesErgebnisA, ungueltigesErgebnisD, gueltigesErgebnisBC);
      final Predicate<StapelartModel> predicateForInvalidErgebnisse =
          stapelart -> StapelartModel.MBW_D_UNGUELTIG.equals(stapelart);
      Mockito.when(ergebnisseService.getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID))
          .thenReturn(ergebnisse);
      Mockito.when(
              wahlartPredicateHolder.getPredicateForStapelWithInvalidErgebnisse(WahlartModel.MBW))
          .thenReturn(predicateForInvalidErgebnisse);

      val result = unitUnderTest.getErgebnisse(WAHL_ID, WAHLBEZIRK_ID);

      val expectedResult =
          new ErgebnismeldungsErgebnisseModel(
              List.of(gueltigesErgebnisA, gueltigesErgebnisBC), List.of(ungueltigesErgebnisD));
      Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
      Mockito.verify(ergebnisseService).getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID);
      Mockito.verify(wahlartPredicateHolder, times(2))
          .getPredicateForStapelWithInvalidErgebnisse(WahlartModel.MBW);
    }

    @Test
    void should_returnEmptyCollections_when_noErgebnisseAreAvailable() {
      Mockito.when(ergebnisseService.getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID))
          .thenReturn(List.of());
      Mockito.when(
              wahlartPredicateHolder.getPredicateForStapelWithInvalidErgebnisse(WahlartModel.MBW))
          .thenReturn(stapelart -> false);

      val result = unitUnderTest.getErgebnisse(WAHL_ID, WAHLBEZIRK_ID);

      Assertions.assertThat(result)
          .isEqualTo(
              new ErgebnismeldungsErgebnisseModel(
                  Collections.emptyList(), Collections.emptyList()));
      Mockito.verify(ergebnisseService).getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID);
      Mockito.verify(wahlartPredicateHolder, times(2))
          .getPredicateForStapelWithInvalidErgebnisse(WahlartModel.MBW);
    }
  }

  private ErgebnisseModel ergebnisWithStapelart(final StapelartModel stapelart) {
    return Instancio.of(ErgebnisseModel.class)
        .set(field(ErgebnisseModel::stapelart), stapelart)
        .create();
  }
}

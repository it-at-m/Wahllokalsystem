package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import static org.instancio.Select.field;
import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultStapelErgebnisseMapperTest {

  private static final String WAHL_ID = "wahlID";
  private static final String WAHLBEZIRK_ID = "wahlbezirkID";

  @Mock ErgebnisseService ergebnisseService;

  @Mock WahlartPredicateHolder wahlartPredicateHolder;

  @InjectMocks DefaultStapelErgebnisseMapper unitUnderTest;

  @Nested
  class getErgebnismeldungErgebnisse {

    @Test
    void should_returnErgebnisse_when_getGueltigeAndUngueltigeErgebnisseAreGiven() {
      val gueltigesErgebnisA = ergebnisWithStapelart(StapelartModel.LTW_BZW_A);
      val ungueltigesErgebnisC = ergebnisWithStapelart(StapelartModel.LTW_BZW_C_UNGUELTIG);
      val ungueltigesErgebnisF = ergebnisWithStapelart(StapelartModel.LTW_BZW_F_UNGUELTIG);
      val gueltigesErgebnisC = ergebnisWithStapelart(StapelartModel.LTW_BZW_C_GUELTIG);
      val ergebnisse =
          List.of(
              gueltigesErgebnisA, ungueltigesErgebnisC, gueltigesErgebnisC, ungueltigesErgebnisF);
      final Predicate<StapelartModel> predicateForInvalidErgebnisse =
          (stapelart) ->
              StapelartModel.LTW_BZW_C_UNGUELTIG.equals(stapelart)
                  || StapelartModel.LTW_BZW_F_UNGUELTIG.equals(stapelart);

      Mockito.when(ergebnisseService.getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID))
          .thenReturn(ergebnisse);
      Mockito.when(
              wahlartPredicateHolder.getPredicateForStapelWithInvalidErgebnisse(WahlartModel.LTW))
          .thenReturn(predicateForInvalidErgebnisse);

      val result =
          unitUnderTest.getErgebnismeldungErgebnisse(
              WAHL_ID, WAHLBEZIRK_ID, WahlartModel.LTW, MeldungsartModel.V1);

      val expectedResult =
          new ErgebnismeldungsErgebnisseModel(
              List.of(gueltigesErgebnisA, gueltigesErgebnisC),
              List.of(ungueltigesErgebnisC, ungueltigesErgebnisF));
      Assertions.assertThat(result)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);
      Mockito.verify(ergebnisseService).getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID);
      Mockito.verify(wahlartPredicateHolder, times(2))
          .getPredicateForStapelWithInvalidErgebnisse(WahlartModel.LTW);
    }

    @Test
    void should_returnEmptyCollection_when_noErgebnisseAreAvailable() {
      Mockito.when(ergebnisseService.getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID))
          .thenReturn(List.of());
      Mockito.when(
              wahlartPredicateHolder.getPredicateForStapelWithInvalidErgebnisse(WahlartModel.EUW))
          .thenReturn(stapelart -> false);

      val result =
          unitUnderTest.getErgebnismeldungErgebnisse(
              WAHL_ID, WAHLBEZIRK_ID, WahlartModel.EUW, MeldungsartModel.V1);

      Assertions.assertThat(result)
          .isEqualTo(
              new ErgebnismeldungsErgebnisseModel(
                  Collections.emptyList(), Collections.emptyList()));
      Mockito.verify(ergebnisseService).getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID);
      Mockito.verify(wahlartPredicateHolder, times(2))
          .getPredicateForStapelWithInvalidErgebnisse(WahlartModel.EUW);
    }

    @Test
    void should_propagateIllegalArgumentException_when_wahlartIsNotSupported() {
      val exception = new IllegalArgumentException("Wahlart wird nicht unterstuetzt");
      Mockito.when(ergebnisseService.getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID))
          .thenReturn(List.of());
      Mockito.when(
              wahlartPredicateHolder.getPredicateForStapelWithInvalidErgebnisse(WahlartModel.SVW))
          .thenThrow(exception);

      Assertions.assertThatThrownBy(
              () ->
                  unitUnderTest.getErgebnismeldungErgebnisse(
                      WAHL_ID, WAHLBEZIRK_ID, WahlartModel.SVW, MeldungsartModel.V1))
          .isSameAs(exception);
      Mockito.verify(ergebnisseService).getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID);
      Mockito.verify(wahlartPredicateHolder)
          .getPredicateForStapelWithInvalidErgebnisse(WahlartModel.SVW);
    }
  }

  @Nested
  class CanHandleWahlart {

    @ParameterizedTest
    @EnumSource(WahlartModel.class)
    void should_returnTrue_when_anyWahlartIsGiven(final WahlartModel wahlart) {
      val result = unitUnderTest.canHandleWahlart(wahlart);

      Assertions.assertThat(result).isEqualTo(!WahlartModel.MBW.equals(wahlart));
      Mockito.verifyNoInteractions(ergebnisseService, wahlartPredicateHolder);
    }
  }

  private ErgebnisseModel ergebnisWithStapelart(final StapelartModel stapelart) {
    return Instancio.of(ErgebnisseModel.class)
        .set(field(ErgebnisseModel::stapelart), stapelart)
        .create();
  }
}

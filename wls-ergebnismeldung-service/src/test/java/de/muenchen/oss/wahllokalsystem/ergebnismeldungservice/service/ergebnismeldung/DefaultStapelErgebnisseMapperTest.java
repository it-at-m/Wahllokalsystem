package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import static org.instancio.Select.field;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.WahlartPredicateHolder;
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
  class GetGueltigeErgebnisse {

    @Test
    void should_returnOnlyErgebnisseWithoutInvalidStapelart_when_getGueltigeErgebnisseIsCalled() {
      val gueltigesErgebnisA = ergebnisWithStapelart(StapelartModel.LTW_BZW_A);
      val ungueltigesErgebnis = ergebnisWithStapelart(StapelartModel.LTW_BZW_C_UNGUELTIG);
      val gueltigesErgebnisC = ergebnisWithStapelart(StapelartModel.LTW_BZW_C_GUELTIG);
      val ergebnisse = List.of(gueltigesErgebnisA, ungueltigesErgebnis, gueltigesErgebnisC);
      final Predicate<StapelartModel> predicateForInvalidErgebnisse =
          Predicate.isEqual(StapelartModel.LTW_BZW_C_UNGUELTIG);

      Mockito.when(ergebnisseService.getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID))
          .thenReturn(ergebnisse);
      Mockito.when(
              wahlartPredicateHolder.getPredicateForStapelWithInvalidErgebnisse(WahlartModel.LTW))
          .thenReturn(predicateForInvalidErgebnisse);

      val result =
          unitUnderTest.getGueltigeErgebnisse(
              WAHL_ID, WAHLBEZIRK_ID, WahlartModel.LTW, MeldungsartModel.V1);

      Assertions.assertThat(result).containsExactly(gueltigesErgebnisA, gueltigesErgebnisC);
      Mockito.verify(ergebnisseService).getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID);
      Mockito.verify(wahlartPredicateHolder)
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
          unitUnderTest.getGueltigeErgebnisse(
              WAHL_ID, WAHLBEZIRK_ID, WahlartModel.EUW, MeldungsartModel.V1);

      Assertions.assertThat(result).isEmpty();
      Mockito.verify(ergebnisseService).getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID);
      Mockito.verify(wahlartPredicateHolder)
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
                  unitUnderTest.getGueltigeErgebnisse(
                      WAHL_ID, WAHLBEZIRK_ID, WahlartModel.SVW, MeldungsartModel.V1))
          .isSameAs(exception);
      Mockito.verify(ergebnisseService).getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID);
      Mockito.verify(wahlartPredicateHolder)
          .getPredicateForStapelWithInvalidErgebnisse(WahlartModel.SVW);
    }
  }

  @Nested
  class GetUngueltigeErgebnisse {

    @Test
    void should_returnOnlyErgebnisseWithInvalidStapelart_when_getUngueltigeErgebnisseIsCalled() {
      val gueltigesErgebnisA = ergebnisWithStapelart(StapelartModel.LTW_BZW_A);
      val ungueltigesErgebnis = ergebnisWithStapelart(StapelartModel.LTW_BZW_C_UNGUELTIG);
      val gueltigesErgebnisC = ergebnisWithStapelart(StapelartModel.LTW_BZW_C_GUELTIG);
      val ergebnisse = List.of(gueltigesErgebnisA, ungueltigesErgebnis, gueltigesErgebnisC);
      final Predicate<StapelartModel> predicateForInvalidErgebnisse =
          Predicate.isEqual(StapelartModel.LTW_BZW_C_UNGUELTIG);

      Mockito.when(ergebnisseService.getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID))
          .thenReturn(ergebnisse);
      Mockito.when(
              wahlartPredicateHolder.getPredicateForStapelWithInvalidErgebnisse(WahlartModel.LTW))
          .thenReturn(predicateForInvalidErgebnisse);

      val result =
          unitUnderTest.getUngueltigeErgebnisse(
              WAHL_ID, WAHLBEZIRK_ID, WahlartModel.LTW, MeldungsartModel.V1);

      Assertions.assertThat(result).containsExactly(ungueltigesErgebnis);
      Mockito.verify(ergebnisseService).getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID);
      Mockito.verify(wahlartPredicateHolder)
          .getPredicateForStapelWithInvalidErgebnisse(WahlartModel.LTW);
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
                  unitUnderTest.getUngueltigeErgebnisse(
                      WAHL_ID, WAHLBEZIRK_ID, WahlartModel.SVW, MeldungsartModel.V1))
          .isSameAs(exception);
      Mockito.verify(ergebnisseService).getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID);
      Mockito.verify(wahlartPredicateHolder)
          .getPredicateForStapelWithInvalidErgebnisse(WahlartModel.SVW);
    }

    @Test
    void should_returnEmptyCollection_when_noErgebnisseAreAvailable() {
      Mockito.when(ergebnisseService.getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID))
          .thenReturn(List.of());
      Mockito.when(
              wahlartPredicateHolder.getPredicateForStapelWithInvalidErgebnisse(WahlartModel.EUW))
          .thenReturn(stapelart -> false);

      val result =
          unitUnderTest.getUngueltigeErgebnisse(
              WAHL_ID, WAHLBEZIRK_ID, WahlartModel.EUW, MeldungsartModel.V1);

      Assertions.assertThat(result).isEmpty();
      Mockito.verify(ergebnisseService).getAllErgebnisse(WAHL_ID, WAHLBEZIRK_ID);
      Mockito.verify(wahlartPredicateHolder)
          .getPredicateForStapelWithInvalidErgebnisse(WahlartModel.EUW);
    }
  }

  @Nested
  class CanHandleWahlart {

    @ParameterizedTest
    @EnumSource(WahlartModel.class)
    void should_returnTrue_when_anyWahlartIsGiven(final WahlartModel wahlart) {
      val result = unitUnderTest.canHandleWahlart(wahlart);

      Assertions.assertThat(result).isTrue();
      Mockito.verifyNoInteractions(ergebnisseService, wahlartPredicateHolder);
    }
  }

  private ErgebnisseModel ergebnisWithStapelart(final StapelartModel stapelart) {
    return Instancio.of(ErgebnisseModel.class)
        .set(field(ErgebnisseModel::stapelart), stapelart)
        .create();
  }
}

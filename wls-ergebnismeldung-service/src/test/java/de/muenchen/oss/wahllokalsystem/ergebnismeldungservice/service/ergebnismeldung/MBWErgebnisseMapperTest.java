package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import java.util.List;
import java.util.stream.Stream;
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
class MBWErgebnisseMapperTest {

  private static final String WAHL_ID = Instancio.create(String.class);
  private static final String WAHLBEZIRK_ID = Instancio.create(String.class);

  @Mock MBWStimmzettelErgebnisseMapper mbwStimmzettelErgebnisseMapper;

  @Mock MBWStapelErgebnisseMapper mbwStapelErgebnisseMapper;

  @InjectMocks MBWErgebnisseMapper unitUnderTest;

  @Nested
  class CanHandleWahlart {

    @ParameterizedTest
    @EnumSource(WahlartModel.class)
    void should_returnTrue_when_wahlartIsMBW(final WahlartModel wahlart) {
      val result = unitUnderTest.canHandleWahlart(wahlart);

      Assertions.assertThat(result).isEqualTo(WahlartModel.MBW.equals(wahlart));
    }
  }

  @Nested
  class GetErgebnismeldungErgebnisse {

    @Test
    void should_combineErgebnisse_when_stimmzettelAndStapelErgebnisseAreGiven() {
      val stimmzettelErgebnisse = Instancio.create(ErgebnismeldungsErgebnisseModel.class);
      val stapelErgebnisse = Instancio.create(ErgebnismeldungsErgebnisseModel.class);
      Mockito.when(mbwStimmzettelErgebnisseMapper.getErgebnisse(WAHL_ID, WAHLBEZIRK_ID))
          .thenReturn(stimmzettelErgebnisse);
      Mockito.when(mbwStapelErgebnisseMapper.getErgebnisse(WAHL_ID, WAHLBEZIRK_ID))
          .thenReturn(stapelErgebnisse);

      val result =
          unitUnderTest.getErgebnismeldungErgebnisse(
              WAHL_ID, WAHLBEZIRK_ID, WahlartModel.MBW, MeldungsartModel.V1);

      val expectedResult =
          new ErgebnismeldungsErgebnisseModel(
              Stream.concat(
                      stimmzettelErgebnisse.gueltigeErgebnisse().stream(),
                      stapelErgebnisse.gueltigeErgebnisse().stream())
                  .toList(),
              Stream.concat(
                      stimmzettelErgebnisse.ungueltigeErgebnisse().stream(),
                      stapelErgebnisse.ungueltigeErgebnisse().stream())
                  .toList());
      Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void should_returnEmptyErgebnisse_when_stimmzettelAndStapelErgebnisseAreEmpty() {
      val stimmzettelErgebnisse = new ErgebnismeldungsErgebnisseModel(List.of(), List.of());
      val stapelErgebnisse = new ErgebnismeldungsErgebnisseModel(List.of(), List.of());
      Mockito.when(mbwStimmzettelErgebnisseMapper.getErgebnisse(WAHL_ID, WAHLBEZIRK_ID))
          .thenReturn(stimmzettelErgebnisse);
      Mockito.when(mbwStapelErgebnisseMapper.getErgebnisse(WAHL_ID, WAHLBEZIRK_ID))
          .thenReturn(stapelErgebnisse);

      val result =
          unitUnderTest.getErgebnismeldungErgebnisse(
              WAHL_ID, WAHLBEZIRK_ID, WahlartModel.MBW, MeldungsartModel.V1);

      val expectedResult = new ErgebnismeldungsErgebnisseModel(List.of(), List.of());
      Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }
  }
}

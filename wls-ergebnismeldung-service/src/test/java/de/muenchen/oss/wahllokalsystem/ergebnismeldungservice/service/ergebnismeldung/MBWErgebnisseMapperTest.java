package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import java.util.List;
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

  private static final String WAHL_ID = "wahlID";
  private static final String WAHLBEZIRK_ID = "wahlbezirkID";

  @Mock MBWStimmzettelErgebnisseMapper mbwStimmzettelErgebnisseMapper;

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
    void should_returnErgebnisseForEveryMBWStapel_when_stimmzettelErgebnisseAreGiven() {
      val mbwErgebnisse = Instancio.create(MBWErgebnisseModel.class);
      Mockito.when(mbwStimmzettelErgebnisseMapper.getErgebnisse(WAHL_ID, WAHLBEZIRK_ID))
          .thenReturn(mbwErgebnisse);

      val result =
          unitUnderTest.getErgebnismeldungErgebnisse(
              WAHL_ID, WAHLBEZIRK_ID, WahlartModel.MBW, MeldungsartModel.V1);

      val expectedResult =
          new ErgebnismeldungsErgebnisseModel(
              List.of(
                  new ErgebnisseModel(
                      WAHLBEZIRK_ID, WAHL_ID, StapelartModel.MBW_A, mbwErgebnisse.stapelA()),
                  new ErgebnisseModel(
                      WAHLBEZIRK_ID, WAHL_ID, StapelartModel.MBW_B, mbwErgebnisse.stapelB()),
                  new ErgebnisseModel(
                      WAHLBEZIRK_ID,
                      WAHL_ID,
                      StapelartModel.MBW_B_C,
                      mbwErgebnisse.stimmenJeKandidatStapelBC())),
              List.of(
                  new ErgebnisseModel(
                      WAHLBEZIRK_ID,
                      WAHL_ID,
                      StapelartModel.MBW_D_UNGUELTIG,
                      mbwErgebnisse.stapelDUngueltig())));
      Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void should_returnEmptyErgebnisseForEveryMBWStapel_when_stimmzettelErgebnisseAreEmpty() {
      val mbwErgebnisse = new MBWErgebnisseModel(List.of(), List.of(), List.of(), List.of());
      Mockito.when(mbwStimmzettelErgebnisseMapper.getErgebnisse(WAHL_ID, WAHLBEZIRK_ID))
          .thenReturn(mbwErgebnisse);

      val result =
          unitUnderTest.getErgebnismeldungErgebnisse(
              WAHL_ID, WAHLBEZIRK_ID, WahlartModel.MBW, MeldungsartModel.V1);

      val expectedResult =
          new ErgebnismeldungsErgebnisseModel(
              List.of(
                  new ErgebnisseModel(WAHLBEZIRK_ID, WAHL_ID, StapelartModel.MBW_A, List.of()),
                  new ErgebnisseModel(WAHLBEZIRK_ID, WAHL_ID, StapelartModel.MBW_B, List.of()),
                  new ErgebnisseModel(WAHLBEZIRK_ID, WAHL_ID, StapelartModel.MBW_B_C, List.of())),
              List.of(
                  new ErgebnisseModel(
                      WAHLBEZIRK_ID, WAHL_ID, StapelartModel.MBW_D_UNGUELTIG, List.of())));
      Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }
  }
}

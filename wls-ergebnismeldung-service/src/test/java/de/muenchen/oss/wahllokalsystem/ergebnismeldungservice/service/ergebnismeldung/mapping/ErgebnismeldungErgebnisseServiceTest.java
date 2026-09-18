package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.mapping;

import static org.mockito.ArgumentMatchers.any;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ErgebnismeldungErgebnisseServiceTest {

  private static final String WAHL_ID = Instancio.create(String.class);
  private static final String WAHLBEZIRK_ID = Instancio.create(String.class);

  @Mock ErgebnismeldungsErgebnisseMapper firstErgebnisseMapper;

  @Mock ErgebnismeldungsErgebnisseMapper secondErgebnisseMapper;

  ErgebnismeldungErgebnisseService unitUnderTest;

  @BeforeEach
  void setup() {
    unitUnderTest =
        new ErgebnismeldungErgebnisseService(
            List.of(firstErgebnisseMapper, secondErgebnisseMapper));
  }

  @Nested
  class GetErgebnisse {

    @Test
    void should_returnErgebnisseOfFirstMatchingMapper_when_mapperCanHandleWahlart() {
      val expectedResult = Instancio.create(ErgebnismeldungsErgebnisseModel.class);
      Mockito.when(firstErgebnisseMapper.canHandleWahlart(WahlartModel.MBW)).thenReturn(true);
      Mockito.when(
              firstErgebnisseMapper.getErgebnismeldungErgebnisse(
                  WAHL_ID, WAHLBEZIRK_ID, WahlartModel.MBW, MeldungsartModel.V1))
          .thenReturn(expectedResult);

      val result =
          unitUnderTest.getErgebnisse(
              WAHL_ID, WAHLBEZIRK_ID, WahlartModel.MBW, MeldungsartModel.V1);

      Assertions.assertThat(result).isSameAs(expectedResult);
      Mockito.verify(firstErgebnisseMapper).canHandleWahlart(WahlartModel.MBW);
      Mockito.verify(firstErgebnisseMapper)
          .getErgebnismeldungErgebnisse(
              WAHL_ID, WAHLBEZIRK_ID, WahlartModel.MBW, MeldungsartModel.V1);
      Mockito.verifyNoInteractions(secondErgebnisseMapper);
    }

    @Test
    void should_returnErgebnisseOfLaterMatchingMapper_when_precedingMapperCannotHandleWahlart() {
      val expectedResult = Instancio.create(ErgebnismeldungsErgebnisseModel.class);
      Mockito.when(firstErgebnisseMapper.canHandleWahlart(WahlartModel.MBW)).thenReturn(false);
      Mockito.when(secondErgebnisseMapper.canHandleWahlart(WahlartModel.MBW)).thenReturn(true);
      Mockito.when(
              secondErgebnisseMapper.getErgebnismeldungErgebnisse(
                  WAHL_ID, WAHLBEZIRK_ID, WahlartModel.MBW, MeldungsartModel.V3))
          .thenReturn(expectedResult);

      val result =
          unitUnderTest.getErgebnisse(
              WAHL_ID, WAHLBEZIRK_ID, WahlartModel.MBW, MeldungsartModel.V3);

      Assertions.assertThat(result).isSameAs(expectedResult);
      Mockito.verify(firstErgebnisseMapper, Mockito.never())
          .getErgebnismeldungErgebnisse(any(), any(), any(), any());
    }

    @Test
    void should_throwIllegalArgumentException_when_noMapperCanHandleWahlart() {
      Mockito.when(firstErgebnisseMapper.canHandleWahlart(WahlartModel.MBW)).thenReturn(false);
      Mockito.when(secondErgebnisseMapper.canHandleWahlart(WahlartModel.MBW)).thenReturn(false);

      Assertions.assertThatThrownBy(
              () ->
                  unitUnderTest.getErgebnisse(
                      WAHL_ID, WAHLBEZIRK_ID, WahlartModel.MBW, MeldungsartModel.V1))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("No mapper found for wahlart MBW");

      Mockito.verify(firstErgebnisseMapper, Mockito.never())
          .getErgebnismeldungErgebnisse(any(), any(), any(), any());
      Mockito.verify(secondErgebnisseMapper, Mockito.never())
          .getErgebnismeldungErgebnisse(any(), any(), any(), any());
    }
  }
}

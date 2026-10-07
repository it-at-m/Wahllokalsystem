package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ergebnisseProvider;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.KandidatStimmenAnzahlModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.MBWStimmzettelService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.WahlvorschlagStimmzettelAnzahlModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
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
class MBWStimmzettelErgebnismeldungsErgebnisseProviderTest {

  private static final String WAHL_ID = Instancio.create(String.class);
  private static final String WAHLBEZIRK_ID = Instancio.create(String.class);

  @Mock MBWStimmzettelService mbwStimmzettelService;

  @InjectMocks MBWStimmzettelErgebnismeldungsErgebnisseProvider unitUnderTest;

  @Nested
  class GetErgebnisse {

    @Test
    void should_returnResult_when_serviceReturnsNoData() {
      val bezirkUndWahlID = new BezirkUndWahlID(WAHL_ID, WAHLBEZIRK_ID);
      Mockito.when(mbwStimmzettelService.getStapelA(bezirkUndWahlID)).thenReturn(List.of());
      Mockito.when(mbwStimmzettelService.getStapelB(bezirkUndWahlID)).thenReturn(List.of());
      Mockito.when(mbwStimmzettelService.getStapelBC(bezirkUndWahlID)).thenReturn(List.of());
      Mockito.when(mbwStimmzettelService.getStapelD(bezirkUndWahlID)).thenReturn(0L);

      val result = unitUnderTest.getErgebnisse(WAHL_ID, WAHLBEZIRK_ID, MeldungsartModel.V1);

      val expectedResult =
          new ErgebnismeldungsErgebnisseModel(
              List.of(
                  new ErgebnisseModel(WAHLBEZIRK_ID, WAHL_ID, StapelartModel.MBW_A, List.of()),
                  new ErgebnisseModel(WAHLBEZIRK_ID, WAHL_ID, StapelartModel.MBW_B, List.of()),
                  new ErgebnisseModel(WAHLBEZIRK_ID, WAHL_ID, StapelartModel.MBW_B_C, List.of())),
              List.of(
                  new ErgebnisseModel(
                      WAHLBEZIRK_ID,
                      WAHL_ID,
                      StapelartModel.MBW_D_UNGUELTIG,
                      List.of(new ErgebnisModel(null, null, -1L, 0L, null)))));
      Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void should_returnResultWithStapelBC_when_serviceReturnsDataForNiederschrift() {
      val bezirkUndWahlID = new BezirkUndWahlID(WAHL_ID, WAHLBEZIRK_ID);
      val stapelA =
          List.of(
              new WahlvorschlagStimmzettelAnzahlModel("wahlvorschlagIDA", 3L),
              new WahlvorschlagStimmzettelAnzahlModel("wahlvorschlagIDB", 5L));
      val stapelB = List.of(new WahlvorschlagStimmzettelAnzahlModel("wahlvorschlagIDC", 7L));
      val stapelBC =
          List.of(
              new KandidatStimmenAnzahlModel("wahlvorschlagIDA", "kandidatID1", 11L),
              new KandidatStimmenAnzahlModel("wahlvorschlagIDB", "kandidatID2", 13L));

      Mockito.when(mbwStimmzettelService.getStapelA(bezirkUndWahlID)).thenReturn(stapelA);
      Mockito.when(mbwStimmzettelService.getStapelB(bezirkUndWahlID)).thenReturn(stapelB);
      Mockito.when(mbwStimmzettelService.getStapelBC(bezirkUndWahlID)).thenReturn(stapelBC);
      Mockito.when(mbwStimmzettelService.getStapelD(bezirkUndWahlID)).thenReturn(17L);

      val result = unitUnderTest.getErgebnisse(WAHL_ID, WAHLBEZIRK_ID, MeldungsartModel.V1);

      val expectedResult =
          new ErgebnismeldungsErgebnisseModel(
              List.of(
                  new ErgebnisseModel(
                      WAHLBEZIRK_ID,
                      WAHL_ID,
                      StapelartModel.MBW_A,
                      List.of(
                          new ErgebnisModel("wahlvorschlagIDA", null, -1L, 3L, null),
                          new ErgebnisModel("wahlvorschlagIDB", null, -1L, 5L, null))),
                  new ErgebnisseModel(
                      WAHLBEZIRK_ID,
                      WAHL_ID,
                      StapelartModel.MBW_B,
                      List.of(new ErgebnisModel("wahlvorschlagIDC", null, -1L, 7L, null))),
                  new ErgebnisseModel(
                      WAHLBEZIRK_ID,
                      WAHL_ID,
                      StapelartModel.MBW_B_C,
                      List.of(
                          new ErgebnisModel("wahlvorschlagIDA", "kandidatID1", -1L, 11L, null),
                          new ErgebnisModel("wahlvorschlagIDB", "kandidatID2", -1L, 13L, null)))),
              List.of(
                  new ErgebnisseModel(
                      WAHLBEZIRK_ID,
                      WAHL_ID,
                      StapelartModel.MBW_D_UNGUELTIG,
                      List.of(new ErgebnisModel(null, null, -1L, 17L, null)))));
      Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void should_returnResultWithoutStapelBC_when_serviceReturnsDataForSchnellmeldung() {
      val bezirkUndWahlID = new BezirkUndWahlID(WAHL_ID, WAHLBEZIRK_ID);
      val stapelA =
          List.of(
              new WahlvorschlagStimmzettelAnzahlModel("wahlvorschlagIDA", 3L),
              new WahlvorschlagStimmzettelAnzahlModel("wahlvorschlagIDB", 5L));
      val stapelB = List.of(new WahlvorschlagStimmzettelAnzahlModel("wahlvorschlagIDC", 7L));

      Mockito.when(mbwStimmzettelService.getStapelA(bezirkUndWahlID)).thenReturn(stapelA);
      Mockito.when(mbwStimmzettelService.getStapelB(bezirkUndWahlID)).thenReturn(stapelB);
      Mockito.when(mbwStimmzettelService.getStapelD(bezirkUndWahlID)).thenReturn(17L);

      val result = unitUnderTest.getErgebnisse(WAHL_ID, WAHLBEZIRK_ID, MeldungsartModel.V3);

      val expectedResult =
          new ErgebnismeldungsErgebnisseModel(
              List.of(
                  new ErgebnisseModel(
                      WAHLBEZIRK_ID,
                      WAHL_ID,
                      StapelartModel.MBW_A,
                      List.of(
                          new ErgebnisModel("wahlvorschlagIDA", null, -1L, 3L, null),
                          new ErgebnisModel("wahlvorschlagIDB", null, -1L, 5L, null))),
                  new ErgebnisseModel(
                      WAHLBEZIRK_ID,
                      WAHL_ID,
                      StapelartModel.MBW_B,
                      List.of(new ErgebnisModel("wahlvorschlagIDC", null, -1L, 7L, null)))),
              List.of(
                  new ErgebnisseModel(
                      WAHLBEZIRK_ID,
                      WAHL_ID,
                      StapelartModel.MBW_D_UNGUELTIG,
                      List.of(new ErgebnisModel(null, null, -1L, 17L, null)))));
      Assertions.assertThat(result).isEqualTo(expectedResult);
    }
  }
}

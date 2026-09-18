package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.KandidatStimmenAnzahlModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.WahlvorschlagStimmzettelAnzahlModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MBWStimmzettelErgebnisseMapperTest {

  private static final String WAHL_ID = "wahlID";
  private static final String WAHLBEZIRK_ID = "wahlbezirkID";

  @Mock StimmzettelService stimmzettelService;

  @InjectMocks MBWStimmzettelErgebnisseMapper unitUnderTest;

  @Nested
  class GetErgebnisse {

    @Test
    void should_returnResult_when_serviceReturnsNoData() {
      val bezirkUndWahlID = new BezirkUndWahlID(WAHL_ID, WAHLBEZIRK_ID);
      Mockito.when(
              stimmzettelService
                  .getCountByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagSelected(
                      bezirkUndWahlID))
          .thenReturn(List.of());
      Mockito.when(
              stimmzettelService
                  .countByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagThatHasChanges(
                      bezirkUndWahlID))
          .thenReturn(List.of());
      Mockito.when(stimmzettelService.getKandidatVotes(bezirkUndWahlID)).thenReturn(List.of());
      Mockito.when(stimmzettelService.getCountUngueltige(bezirkUndWahlID)).thenReturn(0L);

      val result = unitUnderTest.getErgebnisse(WAHL_ID, WAHLBEZIRK_ID);

      val expectedResult =
          new MBWErgebnisseModel(
              List.of(),
              List.of(),
              List.of(new ErgebnisModel(null, null, null, 0L, null)),
              List.of());
      Assertions.assertThat(result).isEqualTo(expectedResult);
      verifyStimmzettelServiceWasCalledWith(bezirkUndWahlID);
    }

    @Test
    void should_returnResult_when_serviceReturnsData() {
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

      Mockito.when(
              stimmzettelService
                  .getCountByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagSelected(
                      bezirkUndWahlID))
          .thenReturn(stapelA);
      Mockito.when(
              stimmzettelService
                  .countByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagThatHasChanges(
                      bezirkUndWahlID))
          .thenReturn(stapelB);
      Mockito.when(stimmzettelService.getKandidatVotes(bezirkUndWahlID)).thenReturn(stapelBC);
      Mockito.when(stimmzettelService.getCountUngueltige(bezirkUndWahlID)).thenReturn(17L);

      val result = unitUnderTest.getErgebnisse(WAHL_ID, WAHLBEZIRK_ID);

      val expectedResult =
          new MBWErgebnisseModel(
              List.of(
                  new ErgebnisModel("wahlvorschlagIDA", null, null, 3L, null),
                  new ErgebnisModel("wahlvorschlagIDB", null, null, 5L, null)),
              List.of(new ErgebnisModel("wahlvorschlagIDC", null, null, 7L, null)),
              List.of(new ErgebnisModel(null, null, null, 17L, null)),
              List.of(
                  new ErgebnisModel("wahlvorschlagIDA", "kandidatID1", null, 11L, null),
                  new ErgebnisModel("wahlvorschlagIDB", "kandidatID2", null, 13L, null)));
      Assertions.assertThat(result).isEqualTo(expectedResult);
      verifyStimmzettelServiceWasCalledWith(bezirkUndWahlID);
    }
  }

  private void verifyStimmzettelServiceWasCalledWith(final BezirkUndWahlID bezirkUndWahlID) {
    Mockito.verify(stimmzettelService)
        .getCountByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagSelected(bezirkUndWahlID);
    Mockito.verify(stimmzettelService)
        .countByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagThatHasChanges(
            bezirkUndWahlID);
    Mockito.verify(stimmzettelService).getKandidatVotes(bezirkUndWahlID);
    Mockito.verify(stimmzettelService).getCountUngueltige(bezirkUndWahlID);
  }
}

package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.nachlieferungsbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagIdUndWahlbezirkId;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.nachlieferungsbezirke.Nachlieferungsbezirk;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.nachlieferungsbezirke.NachlieferungsbezirkeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlbezirke.WahlbezirkeValidator;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import java.util.Optional;
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
public class NachlieferungsbezirkeServiceTest {

  @Mock NachlieferungsbezirkeRepository nachlieferungsbezirkeRepository;

  @Mock WahlbezirkeValidator wahlbezirkeValidator;

  @Mock ExceptionFactory exceptionFactory;

  @InjectMocks NachlieferungsbezirkeService unitUnderTest;

  @Nested
  class IsNachlieferungsbezirk {

    @Test
    void should_returnTrue_when_wahlbezirkIsNachlieferungsbezirk() {
      val wahltagID = "wahltagID";
      val wahlbezirkID = "wahlbezirkID";
      val mockedNachlieferungsbezirk =
          new Nachlieferungsbezirk(new WahltagIdUndWahlbezirkId(wahltagID, wahlbezirkID));

      Mockito.when(
              nachlieferungsbezirkeRepository.findById(
                  new WahltagIdUndWahlbezirkId(wahltagID, wahlbezirkID)))
          .thenReturn(Optional.of(mockedNachlieferungsbezirk));

      val result = unitUnderTest.isNachlieferungsbezirk(wahltagID, wahlbezirkID);

      Assertions.assertThat(result).isEqualTo(true);

      Mockito.verify(wahlbezirkeValidator).validWahltagIDParamOrThrow(wahltagID);
    }

    @Test
    void should_returnFalse_when_wahlbezirkIsNoNachlieferungsbezirk() {
      val wahltagID = "wahltagID";
      val wahlbezirkID = "wahlbezirkID";

      Mockito.when(
              nachlieferungsbezirkeRepository.findById(
                  new WahltagIdUndWahlbezirkId(wahltagID, wahlbezirkID)))
          .thenReturn(Optional.empty());

      val result = unitUnderTest.isNachlieferungsbezirk(wahltagID, wahlbezirkID);

      Assertions.assertThat(result).isEqualTo(false);

      Mockito.verify(wahlbezirkeValidator).validWahltagIDParamOrThrow(wahltagID);
    }
  }

  @Nested
  class SetNachlieferungsbezirke {

    @Test
    void should_saveNachlieferungsbezirk_when_noDataExistsForWahltag() {
      val wahltagID = "wahltagID";
      val wahlbezirkID = "wahlbezirkID";

      Mockito.when(
              nachlieferungsbezirkeRepository.findByWahltagIdUndWahlbezirkId_WahltagID(wahltagID))
          .thenReturn(List.of());

      Assertions.assertThatNoException()
          .isThrownBy(
              () -> unitUnderTest.setNachlieferungsbezirke(wahltagID, List.of(wahlbezirkID)));

      Mockito.verify(wahlbezirkeValidator).validWahltagIDParamOrThrow(wahltagID);
      Mockito.verify(nachlieferungsbezirkeRepository)
          .save(new Nachlieferungsbezirk(new WahltagIdUndWahlbezirkId(wahltagID, wahlbezirkID)));
    }

    @Test
    void should_saveMultipleNachlieferungsbezirke_when_noDataExistsForWahltag() {
      val wahltagID = "wahltagID";
      val wahlbezirkID1 = "wahlbezirkID1";
      val wahlbezirkID2 = "wahlbezirkID2";
      val wahlbezirkID3 = "wahlbezirkID3";

      Mockito.when(
              nachlieferungsbezirkeRepository.findByWahltagIdUndWahlbezirkId_WahltagID(wahltagID))
          .thenReturn(List.of());

      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  unitUnderTest.setNachlieferungsbezirke(
                      wahltagID, List.of(wahlbezirkID1, wahlbezirkID2, wahlbezirkID3)));

      Mockito.verify(wahlbezirkeValidator).validWahltagIDParamOrThrow(wahltagID);
      Mockito.verify(nachlieferungsbezirkeRepository)
          .save(new Nachlieferungsbezirk(new WahltagIdUndWahlbezirkId(wahltagID, wahlbezirkID1)));
      Mockito.verify(nachlieferungsbezirkeRepository)
          .save(new Nachlieferungsbezirk(new WahltagIdUndWahlbezirkId(wahltagID, wahlbezirkID2)));
      Mockito.verify(nachlieferungsbezirkeRepository)
          .save(new Nachlieferungsbezirk(new WahltagIdUndWahlbezirkId(wahltagID, wahlbezirkID3)));
    }

    @Test
    void should_overrideNachlieferungsbezirke_when_dataExistsForWahltag() {
      val wahltagID = "wahltagID";
      val wahlbezirkID1 = "wahlbezirkID1";
      val wahlbezirkID2 = "wahlbezirkID2";
      val existingNachlieferungsbezirke =
          List.of(new Nachlieferungsbezirk(new WahltagIdUndWahlbezirkId(wahltagID, wahlbezirkID1)));

      Mockito.when(
              nachlieferungsbezirkeRepository.findByWahltagIdUndWahlbezirkId_WahltagID(wahltagID))
          .thenReturn(existingNachlieferungsbezirke);

      Assertions.assertThatNoException()
          .isThrownBy(
              () -> unitUnderTest.setNachlieferungsbezirke(wahltagID, List.of(wahlbezirkID2)));

      Mockito.verify(wahlbezirkeValidator).validWahltagIDParamOrThrow(wahltagID);
      Mockito.verify(nachlieferungsbezirkeRepository).deleteAll(existingNachlieferungsbezirke);
      Mockito.verify(nachlieferungsbezirkeRepository)
          .save(new Nachlieferungsbezirk(new WahltagIdUndWahlbezirkId(wahltagID, wahlbezirkID2)));
    }

    @Test
    void should_throwTechnischeWlsException_when_savingFailed() {
      val wahltagID = "wahltagID";
      val wahlbezirkID = "wahlbezirkID";

      val mockedRepoSaveException = new RuntimeException("saving failed");
      val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("");
      val mockedNachlieferungsbezirk =
          new Nachlieferungsbezirk(new WahltagIdUndWahlbezirkId(wahltagID, wahlbezirkID));

      Mockito.doThrow(mockedRepoSaveException)
          .when(nachlieferungsbezirkeRepository)
          .save(mockedNachlieferungsbezirk);
      Mockito.when(
              exceptionFactory.createTechnischeWlsException(
                  ExceptionConstants.POSTNACHLIEFERUNGSBEZIRKE_SPEICHERN_NICHT_ERFOLGREICH))
          .thenReturn(mockedWlsException);

      Assertions.assertThatThrownBy(
              () -> unitUnderTest.setNachlieferungsbezirke(wahltagID, List.of(wahlbezirkID)))
          .isSameAs(mockedWlsException);
    }
  }
}

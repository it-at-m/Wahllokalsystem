package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisseRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
public class EreignisServiceTest {

  @Mock EreignisseRepository ereignisRepository;

  @Mock EreignisseModelMapper ereignisModelMapper;

  @Mock EreignisValidator ereignisValidator;

  @Mock ExceptionFactory exceptionFactory;

  @InjectMocks EreignisService unitUnderTest;

  @Nested
  class GetEreignisse {

    @Test
    void should_returnEreignisseModel_when_givenValidWahlbezirkID() {
      val wahlbezirkID = "wahlbezirkID";
      val keineVorfaelle = false;
      val keineVorkommnisse = true;

      val mockedEreignis = TestdataFactory.CreateEreignisEntity.withData("beschreibung");
      val mockedEreignisse =
          TestdataFactory.CreateEreignisseEntity.withData(wahlbezirkID, Set.of(mockedEreignis));

      val mockedEreignisModelList =
          List.of(TestdataFactory.CreateEreignisModel.withEreignisart(EreignisartModel.VORFALL));
      val expectedEreignisseModel =
          TestdataFactory.CreateWahlbezirkEreignisseModel.withData(
              wahlbezirkID, keineVorfaelle, keineVorkommnisse, mockedEreignisModelList);

      Mockito.when(ereignisRepository.findByWahlbezirkID(wahlbezirkID))
          .thenReturn(Optional.of(mockedEreignisse));
      Mockito.when(ereignisModelMapper.toModel(mockedEreignisse))
          .thenReturn(expectedEreignisseModel);

      val result = unitUnderTest.getEreignisse(wahlbezirkID);
      Assertions.assertThat(result).isEqualTo(Optional.of(expectedEreignisseModel));

      Mockito.verify(ereignisValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
    }

    @Test
    void should_returnEmptyResponse_when_noDataFound() {
      val wahlbezirkID = "wahlbezirkID";

      Mockito.when(ereignisRepository.findByWahlbezirkID(wahlbezirkID))
          .thenReturn(Optional.empty());

      val result = unitUnderTest.getEreignisse(wahlbezirkID);
      Assertions.assertThat(result).isEmpty();
    }
  }

  @Nested
  class PostEreignisse {

    @Test
    void should_notThrowException_when_newDataIsSaved() {
      val wahlbezirkID = "wahlbezirkID";

      val mockedEreignisModelList =
          List.of(TestdataFactory.CreateEreignisModel.withData("beschreibung"));
      val mockedEreignisseWriteModel =
          TestdataFactory.CreateEreignisseWriteModel.withData(
              wahlbezirkID, mockedEreignisModelList);

      val mockedEreignis = TestdataFactory.CreateEreignisEntity.withData("beschreibung");
      val mockedEreignisse =
          TestdataFactory.CreateEreignisseEntity.withData(wahlbezirkID, Set.of(mockedEreignis));

      Mockito.when(ereignisModelMapper.toEntity(mockedEreignisseWriteModel))
          .thenReturn(mockedEreignisse);

      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.postEreignisse(mockedEreignisseWriteModel));

      Mockito.verify(ereignisValidator)
          .validEreignisAndWahlbezirkIDOrThrow(mockedEreignisseWriteModel);
      Mockito.verify(ereignisRepository)
          .deleteByWahlbezirkID(mockedEreignisseWriteModel.wahlbezirkID());
      Mockito.verify(ereignisRepository).save(mockedEreignisse);
    }

    @Test
    void should_throwWlsException_when_savingFailed() {
      val wahlbezirkID = "wahlbezirkID";

      val mockedEreignisModelList =
          List.of(TestdataFactory.CreateEreignisModel.withData("beschreibung"));
      val mockedEreignisseWriteModel =
          TestdataFactory.CreateEreignisseWriteModel.withData(
              wahlbezirkID, mockedEreignisModelList);

      val mockedEreignis = TestdataFactory.CreateEreignisEntity.withData("beschreibung");
      val mockedEreignisse =
          TestdataFactory.CreateEreignisseEntity.withData(wahlbezirkID, Set.of(mockedEreignis));

      val mockedRepoSaveException = new RuntimeException("saving failed");
      val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("");

      Mockito.when(ereignisModelMapper.toEntity(mockedEreignisseWriteModel))
          .thenReturn(mockedEreignisse);
      Mockito.doThrow(mockedRepoSaveException).when(ereignisRepository).save(mockedEreignisse);
      Mockito.when(
              exceptionFactory.createTechnischeWlsException(
                  ExceptionConstants.SAVEEREIGNIS_UNSAVABLE))
          .thenReturn(mockedWlsException);

      Assertions.assertThatThrownBy(() -> unitUnderTest.postEreignisse(mockedEreignisseWriteModel))
          .isSameAs(mockedWlsException);
    }
  }
}

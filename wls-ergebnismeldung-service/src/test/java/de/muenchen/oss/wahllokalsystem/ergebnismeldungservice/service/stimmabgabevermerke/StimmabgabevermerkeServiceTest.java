package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.BezirkUndWahlIDUndWaehlerverzeichnisnummer;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmabgabevermerke;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.StimmabgabevermerkeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Collections;
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
class StimmabgabevermerkeServiceTest {

  @Mock StimmabgabevermerkeModelMapper stimmabgabevermerkeModelMapper;

  @Mock StimmabgabevermerkeRepository stimmabgabevermerkeRepository;

  @Mock StimmabgabevermerkeValidator stimmabgabevermerkeValidator;

  @Mock ExceptionFactory exceptionFactory;

  @InjectMocks StimmabgabevermerkeService unitUnderTest;

  @Nested
  class GetStimmabgabevermerke {

    @Test
    void should_returnOptionalWithData_when_dataIsFoundInRepository() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 1L;
      val id =
          new BezirkUndWahlIDUndWaehlerverzeichnisnummer(
              wahlbezirkID, wahlID, waehlerverzeichnisNummer);

      val mockedRepoResponse =
          new Stimmabgabevermerke(
              new BezirkUndWahlIDUndWaehlerverzeichnisnummer(
                  wahlbezirkID, wahlID, waehlerverzeichnisNummer),
              Collections.emptySet(),
              Collections.emptySet());
      val mockedRepoResponseAsModel =
          new StimmabgabevermerkeModel(
              wahlbezirkID,
              wahlID,
              waehlerverzeichnisNummer,
              Collections.emptySet(),
              Collections.emptySet());

      Mockito.when(stimmabgabevermerkeRepository.findById(id))
          .thenReturn(Optional.of(mockedRepoResponse));
      Mockito.when(stimmabgabevermerkeModelMapper.toModel(mockedRepoResponse))
          .thenReturn(mockedRepoResponseAsModel);

      val result =
          unitUnderTest.getStimmabgabevermerke(wahlbezirkID, wahlID, waehlerverzeichnisNummer);

      Assertions.assertThat(result).isEqualTo(Optional.of(mockedRepoResponseAsModel));
    }

    @Test
    void should_returnEmptyOptional_when_noDataIsFoundInRepository() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 1L;
      val id =
          new BezirkUndWahlIDUndWaehlerverzeichnisnummer(
              wahlbezirkID, wahlID, waehlerverzeichnisNummer);

      Mockito.when(stimmabgabevermerkeRepository.findById(id)).thenReturn(Optional.empty());

      val result =
          unitUnderTest.getStimmabgabevermerke(wahlbezirkID, wahlID, waehlerverzeichnisNummer);

      Assertions.assertThat(result).isEmpty();
    }

    @Test
    void should_callValidatorWithId_when_methodIsCalled() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 1L;
      val id =
          new BezirkUndWahlIDUndWaehlerverzeichnisnummer(
              wahlbezirkID, wahlID, waehlerverzeichnisNummer);

      val mockedFachlicheWlsExceptionForIdValidation =
          FachlicheWlsException.withCode("000").buildWithMessage("");
      Mockito.when(
              exceptionFactory.createFachlicheWlsException(
                  ExceptionConstants.GET_STIMMABGABEVERMERKE_PARAMETER_UNVOLLSTAENDIG))
          .thenReturn(mockedFachlicheWlsExceptionForIdValidation);

      unitUnderTest.getStimmabgabevermerke(wahlbezirkID, wahlID, waehlerverzeichnisNummer);

      Mockito.verify(stimmabgabevermerkeValidator)
          .validBezirkIDUndWaehlerverzeichnisnummerOrThrow(
              id, mockedFachlicheWlsExceptionForIdValidation);
    }
  }

  @Nested
  class PostStimmabgabevermerke {

    @Test
    void should_callValidators_when_calledWithParameters() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 1L;
      val id =
          new BezirkUndWahlIDUndWaehlerverzeichnisnummer(
              wahlbezirkID, wahlID, waehlerverzeichnisNummer);
      val stimmabgabevermerke =
          new StimmabgabevermerkeModel(
              wahlbezirkID,
              wahlID,
              waehlerverzeichnisNummer,
              Collections.emptySet(),
              Collections.emptySet());

      val mockedFachlicheWlsExceptionForIdValidation =
          FachlicheWlsException.withCode("000").buildWithMessage("");
      Mockito.when(
              exceptionFactory.createFachlicheWlsException(
                  ExceptionConstants.POST_STIMMABGABEVERMERKE_PARAMETER_UNVOLLSTAENDIG))
          .thenReturn(mockedFachlicheWlsExceptionForIdValidation);

      unitUnderTest.postStimmabgabevermerke(stimmabgabevermerke);

      Mockito.verify(stimmabgabevermerkeValidator)
          .validStimmabgabevermerkeOrThrow(stimmabgabevermerke);
      Mockito.verify(stimmabgabevermerkeValidator)
          .validBezirkIDUndWaehlerverzeichnisnummerOrThrow(
              id, mockedFachlicheWlsExceptionForIdValidation);
    }

    @Test
    void should_saveMappedDataInRepository_when_stimmabgabevermerkeAreGiven() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 1L;
      val stimmabgabevermerke =
          new StimmabgabevermerkeModel(
              wahlbezirkID,
              wahlID,
              waehlerverzeichnisNummer,
              Collections.emptySet(),
              Collections.emptySet());

      val mockedMappedModelAsEntity = new Stimmabgabevermerke();
      Mockito.when(stimmabgabevermerkeModelMapper.toEntity(stimmabgabevermerke))
          .thenReturn(mockedMappedModelAsEntity);

      unitUnderTest.postStimmabgabevermerke(stimmabgabevermerke);

      Mockito.verify(stimmabgabevermerkeRepository).save(mockedMappedModelAsEntity);
    }

    @Test
    void should_throwTechnischeWlsException_when_repositoryThrowsException() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 1L;
      val stimmabgabevermerke =
          new StimmabgabevermerkeModel(
              wahlbezirkID,
              wahlID,
              waehlerverzeichnisNummer,
              Collections.emptySet(),
              Collections.emptySet());

      val mockedMappedModelAsEntity = new Stimmabgabevermerke();
      val mockedRepositoryException = new RuntimeException("saving failed");
      val mockedThrowWlsException = TechnischeWlsException.withCode("").buildWithMessage("");

      Mockito.when(stimmabgabevermerkeModelMapper.toEntity(stimmabgabevermerke))
          .thenReturn(mockedMappedModelAsEntity);
      Mockito.when(stimmabgabevermerkeRepository.save(mockedMappedModelAsEntity))
          .thenThrow(mockedRepositoryException);
      Mockito.when(
              exceptionFactory.createTechnischeWlsException(
                  ExceptionConstants.STIMMABGABEVERMERKE_UNSAVEABLE))
          .thenReturn(mockedThrowWlsException);

      Assertions.assertThatThrownBy(
              () -> unitUnderTest.postStimmabgabevermerke(stimmabgabevermerke))
          .isSameAs(mockedThrowWlsException);
    }
  }
}

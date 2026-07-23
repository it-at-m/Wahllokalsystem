package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status.ErfassungStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status.StimmzettelerfassungStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status.StimmzettelerfassungStatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StimmzettelerfassungServiceTest {

  @Mock StimmzettelerfassungStatusRepository stimmzettelerfassungStatusRepository;

  @Mock StimmzettelerfassungValidator stimmzettelerfassungValidator;

  @Mock ErfassungStatusModelMapper erfassungStatusModelMapper;

  @Mock ExceptionFactory exceptionFactory;

  @InjectMocks StimmzettelerfassungService unitUnderTest;

  @Nested
  class SaveStimmzettelerfassungStatus {
    @Test
    void should_throwException_when_callingServiceCreateAnExceptionDuringValidation() {
      val id = new BezirkUndWahlID();
      val erfassungStatusModel = ErfassungStatusModel.STE_ABGESCHLOSSEN;
      val erfassungStatusEntity = ErfassungStatus.STE_ABGESCHLOSSEN;

      val mockedWlsException =
          FachlicheWlsException.withCode("").buildWithMessage("validation of parameters failed");
      Mockito.when(
              exceptionFactory.createFachlicheWlsException(
                  ExceptionConstants.POST_STIMMZETTELERFASSUNG_STATUS_PARAMETER_UNVOLLSTAENDIG))
          .thenReturn(mockedWlsException);
      Mockito.doThrow(mockedWlsException)
          .when(stimmzettelerfassungValidator)
          .validBezirkUndWahlIdOrThrow(id, mockedWlsException);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.saveStimmzettelerfassungStatus(id, erfassungStatusModel))
          .isSameAs(mockedWlsException);
    }

    @Test
    void should_saveErfassungStatus_when_called() {
      val id = new BezirkUndWahlID();
      val erfassungStatusModel = ErfassungStatusModel.STE_ABGESCHLOSSEN;
      val erfassungStatusEntity = ErfassungStatus.STE_ABGESCHLOSSEN;

      Mockito.when(erfassungStatusModelMapper.toEntity(erfassungStatusModel))
          .thenReturn(erfassungStatusEntity);

      unitUnderTest.saveStimmzettelerfassungStatus(id, erfassungStatusModel);

      Mockito.verify(stimmzettelerfassungStatusRepository)
          .save(new StimmzettelerfassungStatus(id, erfassungStatusEntity));
    }
  }

  @Nested
  class GetStimmzettelerfassungStatus {
    @Test
    void
        should_throwSubmittedFachlicheWlsExceptionForParameter_when_validatorThrowExceptionDuringValidation() {
      val id = new BezirkUndWahlID();

      val mockedWlsException =
          FachlicheWlsException.withCode("").buildWithMessage("validation of parameters failed");
      Mockito.when(
              exceptionFactory.createFachlicheWlsException(
                  ExceptionConstants.GET_STIMMZETTELERFASSUNG_STATUS_PARAMETER_UNVOLLSTAENDIG))
          .thenReturn(mockedWlsException);
      Mockito.doThrow(mockedWlsException)
          .when(stimmzettelerfassungValidator)
          .validBezirkUndWahlIdOrThrow(id, mockedWlsException);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getStimmzettelerfassungStatus(id))
          .isSameAs(mockedWlsException);
    }

    @Test
    void should_returnStimmzettelumschlaegeModel_when_stimmzettelumschlaegeIsFoundFromRepo() {
      val id = new BezirkUndWahlID();
      val erfassungStatusModel = ErfassungStatusModel.STE_ABGESCHLOSSEN;
      val erfassungStatusEntity = ErfassungStatus.STE_ABGESCHLOSSEN;

      val mockedEntity = new StimmzettelerfassungStatus(id, erfassungStatusEntity);

      Mockito.when(stimmzettelerfassungStatusRepository.findById(id))
          .thenReturn(Optional.of(mockedEntity));
      Mockito.when(erfassungStatusModelMapper.toModel(erfassungStatusEntity))
          .thenReturn(erfassungStatusModel);

      val result = unitUnderTest.getStimmzettelerfassungStatus(id);

      Assertions.assertThat(result).isEqualTo(Optional.of(erfassungStatusModel));
    }

    @Test
    void should_returnEmptyOptional_when_stimmzettelumschlaegeIsNotFoundFromRepo() {
      val id = new BezirkUndWahlID();

      Mockito.when(stimmzettelerfassungStatusRepository.findById(id)).thenReturn(Optional.empty());

      val result = unitUnderTest.getStimmzettelerfassungStatus(id);

      Assertions.assertThat(result).isEmpty();
    }
  }

  @Nested
  class RegisterStimmzettelerfassungStart {

    @Test
    void should_saveStatusInBearbeitung_when_noStatusExists() {
      val id = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(stimmzettelerfassungStatusRepository.findById(id)).thenReturn(Optional.empty());
      Mockito.when(erfassungStatusModelMapper.toEntity(ErfassungStatusModel.STE_BEARBEITUNG))
          .thenReturn(ErfassungStatus.STE_BEARBEITUNG);

      unitUnderTest.registerStimmzettelerfassungStart(id);

      Mockito.verify(stimmzettelerfassungStatusRepository)
          .save(new StimmzettelerfassungStatus(id, ErfassungStatus.STE_BEARBEITUNG));
    }

    @ParameterizedTest
    @MethodSource("enumValuesThatAreNotInBearbeitung")
    void should_saveStatusInBearbeitung_when_statusIsNotInBearbeitung(
        final ArgumentsAccessor arguments) {
      val id = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(stimmzettelerfassungStatusRepository.findById(id))
          .thenReturn(
              Optional.of(
                  new StimmzettelerfassungStatus(id, arguments.get(0, ErfassungStatus.class))));
      Mockito.when(erfassungStatusModelMapper.toEntity(ErfassungStatusModel.STE_BEARBEITUNG))
          .thenReturn(ErfassungStatus.STE_BEARBEITUNG);

      unitUnderTest.registerStimmzettelerfassungStart(id);

      Mockito.verify(stimmzettelerfassungStatusRepository)
          .save(new StimmzettelerfassungStatus(id, ErfassungStatus.STE_BEARBEITUNG));
    }

    public static Stream<Arguments> enumValuesThatAreNotInBearbeitung() {
      return Arrays.stream(ErfassungStatus.values())
          .filter(status -> status != ErfassungStatus.STE_BEARBEITUNG)
          .map(Arguments::of);
    }

    @Test
    void should_doNothing_when_statusIsAlreadyInBearbeitung() {
      val id = Instancio.create(BezirkUndWahlID.class);

      val mockedInBearbeitungStatus =
          new StimmzettelerfassungStatus(id, ErfassungStatus.STE_BEARBEITUNG);
      Mockito.when(stimmzettelerfassungStatusRepository.findById(id))
          .thenReturn(Optional.of(mockedInBearbeitungStatus));

      unitUnderTest.registerStimmzettelerfassungStart(id);

      Mockito.verify(stimmzettelerfassungStatusRepository, Mockito.times(0)).save(Mockito.any());
    }
  }
}

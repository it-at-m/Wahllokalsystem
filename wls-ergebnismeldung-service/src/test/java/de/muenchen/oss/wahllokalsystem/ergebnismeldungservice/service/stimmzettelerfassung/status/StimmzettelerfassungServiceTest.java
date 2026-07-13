package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status.ErfassungStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status.StimmzettelerfassungStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status.StimmzettelerfassungStatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
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
public class StimmzettelerfassungServiceTest {

  @Mock StimmzettelerfassungStatusRepository stimmzettelerfassungStatusRepository;

  @Mock StimmzettelerfassungValidator stimmzettelerfassungValidator;

  @Mock ErfassungStatusModelMapper erfassungStatusModelMapper;

  @Mock ExceptionFactory exceptionFactory;

  @InjectMocks StimmzettelerfassungService unitUnderTest;

  @Nested
  class SaveStimmzettelerfassungStatus {
    @Test
    void should_callValidator_when_callingService() {
      val id = new BezirkUndWahlID();
      val erfassungStatusModel = ErfassungStatusModel.STE_ABGESCHLOSSEN;
      val erfassungStatusEntity = ErfassungStatus.STE_ABGESCHLOSSEN;

      val mockedWlsException =
          FachlicheWlsException.withCode("").buildWithMessage("validation of parameters failed");
      Mockito.when(
              exceptionFactory.createFachlicheWlsException(
                  ExceptionConstants.POST_STIMMZETTELERFASSUNG_STATUS_PARAMETER_UNVOLLSTAENDIG))
          .thenReturn(mockedWlsException);
      Mockito.when(erfassungStatusModelMapper.toEntity(erfassungStatusModel))
          .thenReturn(erfassungStatusEntity);

      unitUnderTest.saveStimmzettelerfassungStatus(id, erfassungStatusModel);

      Mockito.verify(stimmzettelerfassungValidator)
          .validBezirkUndWahlIdOrThrow(eq(id), eq(mockedWlsException));
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
    void should_submitFachlicheWlsExceptionForParameter_when_callingValidator() {
      val id = new BezirkUndWahlID();

      val mockedWlsException =
          FachlicheWlsException.withCode("").buildWithMessage("validation of parameters failed");
      Mockito.when(
              exceptionFactory.createFachlicheWlsException(
                  ExceptionConstants.GET_STIMMZETTELERFASSUNG_STATUS_PARAMETER_UNVOLLSTAENDIG))
          .thenReturn(mockedWlsException);

      unitUnderTest.getStimmzettelerfassungStatus(id);

      Mockito.verify(stimmzettelerfassungValidator)
          .validBezirkUndWahlIdOrThrow(eq(id), eq(mockedWlsException));
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
}

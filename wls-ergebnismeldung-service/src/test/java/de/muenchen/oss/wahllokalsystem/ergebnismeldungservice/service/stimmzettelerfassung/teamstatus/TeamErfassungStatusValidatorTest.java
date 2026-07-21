package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModelValidator;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.function.Supplier;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeamErfassungStatusValidatorTest {

  @Mock ExceptionFactory exceptionFactory;

  @Mock TeamBezirkUndWahlIDModelValidator teamBezirkUndWahlIDModelValidator;

  @InjectMocks TeamErfassungStatusValidator unitUnderTest;

  @Nested
  class IsValidOrThrow {

    @Nested
    class OfTeamBezirkUndWahlIDModel {

      @Captor ArgumentCaptor<Supplier<WlsException>> captor;

      @Test
      void should_useTeamBezirkUndWahlIDValidator_when_called() {
        val id = Instancio.create(TeamBezirkUndWahlIDModel.class);

        unitUnderTest.isValidOrThrow(id);

        Mockito.verify(teamBezirkUndWahlIDModelValidator)
            .isValidOrThrow(Mockito.eq(id), captor.capture());
        Assertions.assertThat(captor.getValue().get())
            .isEqualTo(
                exceptionFactory.createFachlicheWlsException(
                    ExceptionConstants.STIMMZETTELERFASSUNG_TEAM_STATUS_INVALID_IDs));
      }
    }

    @Nested
    class OfTeamErfassungStatusModel {

      @Test
      void should_notThrowAnyException_when_modelIsValid() {
        val modelToValidate = Instancio.create(TeamErfassungStatusModel.class);

        Assertions.assertThatNoException()
            .isThrownBy(() -> unitUnderTest.isValidOrThrow(modelToValidate));
      }

      @Test
      void should_throwException_when_modelIsNull() {
        val mockedWlsException =
            FachlicheWlsException.withCode("000").buildWithMessage("mocked wls exception");

        Mockito.when(
                exceptionFactory.createFachlicheWlsException(
                    ExceptionConstants.STIMMZETTELERFASSUNG_TEAM_STATUS_SAVE_MODEL_IS_MISSING))
            .thenReturn(mockedWlsException);

        Assertions.assertThatException()
            .isThrownBy(() -> unitUnderTest.isValidOrThrow((TeamErfassungStatusModel) null))
            .usingRecursiveComparison()
            .isEqualTo(mockedWlsException);
      }
    }
  }
}

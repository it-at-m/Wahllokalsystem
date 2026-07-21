package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus;

import static org.instancio.Select.field;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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
class TeamErfassungStatusValidatorTest {

  @Mock ExceptionFactory exceptionFactory;

  @InjectMocks TeamErfassungStatusValidator unitUnderTest;

  @Nested
  class IsValidOrThrow {

    @Nested
    class OfWahlbezirkErfassungsteamIDModel {

      @Test
      void should_notThrowAnyException_when_idIsValid() {
        val idToValidate = Instancio.create(TeamBezirkUndWahlIDModel.class);

        Assertions.assertThatNoException()
            .isThrownBy(() -> unitUnderTest.isValidOrThrow(idToValidate));
      }

      @ParameterizedTest(name = "throw exception when {1}")
      @MethodSource("invalidWahlbezirkErfassungsteamID")
      void should_throwException_when_idIsInvalid(final ArgumentsAccessor arguments) {
        val idToValidate = arguments.get(0, TeamBezirkUndWahlIDModel.class);
        val mockedWlsException =
            FachlicheWlsException.withCode("000").buildWithMessage("mocked wls exception");

        Mockito.when(
                exceptionFactory.createFachlicheWlsException(
                    ExceptionConstants.STIMMZETTELERFASSUNG_TEAM_STATUS_INVALID_IDs))
            .thenReturn(mockedWlsException);

        Assertions.assertThatException()
            .isThrownBy(() -> unitUnderTest.isValidOrThrow(idToValidate))
            .usingRecursiveComparison()
            .isEqualTo(mockedWlsException);
      }

      public static Stream<Arguments> invalidWahlbezirkErfassungsteamID() {
        return Stream.of(
            Arguments.of(null, "id is null"),
            Arguments.of(
                Instancio.of(TeamBezirkUndWahlIDModel.class)
                    .set(field(TeamBezirkUndWahlIDModel::wahlID), null)
                    .create(),
                "wahlID is null"),
            Arguments.of(
                Instancio.of(TeamBezirkUndWahlIDModel.class)
                    .set(field(TeamBezirkUndWahlIDModel::wahlID), "")
                    .create(),
                "wahlID is empty string"),
            Arguments.of(
                Instancio.of(TeamBezirkUndWahlIDModel.class)
                    .set(field(TeamBezirkUndWahlIDModel::wahlID), "   ")
                    .create(),
                "wahlID is blank string"),
            Arguments.of(
                Instancio.of(TeamBezirkUndWahlIDModel.class)
                    .set(field(TeamBezirkUndWahlIDModel::wahlbezirkID), null)
                    .create(),
                "wahlbezirkID is null"),
            Arguments.of(
                Instancio.of(TeamBezirkUndWahlIDModel.class)
                    .set(field(TeamBezirkUndWahlIDModel::wahlbezirkID), "")
                    .create(),
                "wahlbezirkID is empty string"),
            Arguments.of(
                Instancio.of(TeamBezirkUndWahlIDModel.class)
                    .set(field(TeamBezirkUndWahlIDModel::wahlbezirkID), "   ")
                    .create(),
                "wahlbezirkID is blank string"),
            Arguments.of(
                Instancio.of(TeamBezirkUndWahlIDModel.class)
                    .set(field(TeamBezirkUndWahlIDModel::teamID), null)
                    .create(),
                "teamID is null"),
            Arguments.of(
                Instancio.of(TeamBezirkUndWahlIDModel.class)
                    .set(field(TeamBezirkUndWahlIDModel::teamID), "")
                    .create(),
                "teamID is empty string"),
            Arguments.of(
                Instancio.of(TeamBezirkUndWahlIDModel.class)
                    .set(field(TeamBezirkUndWahlIDModel::teamID), "   ")
                    .create(),
                "teamID is blank string"));
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

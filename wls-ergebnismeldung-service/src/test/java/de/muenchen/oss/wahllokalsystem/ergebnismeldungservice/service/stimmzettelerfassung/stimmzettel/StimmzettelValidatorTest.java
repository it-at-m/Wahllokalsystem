package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import static org.instancio.Select.field;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.DataConflictException;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
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
class StimmzettelValidatorTest {

  @Mock ExceptionFactory exceptionFactory;

  @InjectMocks StimmzettelValidator unitUnderTest;

  @Nested
  class ValidOrThrow {

    @Nested
    class OfListStimmzettelOfTeamModel {

      @Test
      void should_notThrowAnyException_when_listContainsCorrectItems() {
        val listToValidate =
            List.of(
                Instancio.of(StimmzettelOfTeamModel.class)
                    .set(field(StimmzettelOfTeamModel::stimmzettelkennung), 1)
                    .create(),
                Instancio.of(StimmzettelOfTeamModel.class)
                    .set(field(StimmzettelOfTeamModel::stimmzettelkennung), 2)
                    .create(),
                Instancio.of(StimmzettelOfTeamModel.class)
                    .set(field(StimmzettelOfTeamModel::stimmzettelkennung), 3)
                    .create());

        Assertions.assertThatNoException()
            .isThrownBy(() -> unitUnderTest.validOrThrow(listToValidate));
      }

      @Test
      void should_throwException_when_listIsNull() {
        val mockedWlsException =
            FachlicheWlsException.withCode("000").buildWithMessage("mocked wls exception");
        Mockito.when(
                exceptionFactory.createFachlicheWlsException(ExceptionConstants.STIMMZETTEL_FEHLEN))
            .thenReturn(mockedWlsException);

        Assertions.assertThatException()
            .isThrownBy(() -> unitUnderTest.validOrThrow((List<StimmzettelOfTeamModel>) null))
            .usingRecursiveComparison()
            .isEqualTo(mockedWlsException);
      }

      @Test
      void should_throwException_when_stimmzettelkennungIsNotUnique() {
        val listToValidate =
            List.of(
                Instancio.of(StimmzettelOfTeamModel.class)
                    .set(field(StimmzettelOfTeamModel::stimmzettelkennung), 1)
                    .create(),
                Instancio.of(StimmzettelOfTeamModel.class)
                    .set(field(StimmzettelOfTeamModel::stimmzettelkennung), 2)
                    .create(),
                Instancio.of(StimmzettelOfTeamModel.class)
                    .set(field(StimmzettelOfTeamModel::stimmzettelkennung), 2)
                    .create(),
                Instancio.of(StimmzettelOfTeamModel.class)
                    .set(field(StimmzettelOfTeamModel::stimmzettelkennung), 3)
                    .create());

        Assertions.assertThatException()
            .isThrownBy(() -> unitUnderTest.validOrThrow(listToValidate))
            .usingRecursiveComparison()
            .isEqualTo(new DataConflictException(ExceptionConstants.STIMMZETTELKENNUNG_NON_UNIQUE));
      }
    }

    @Nested
    class OfStimmzettelOwnerModel {

      @Test
      void should_notThrowAnyException_when_dataIsCorrect() {
        Assertions.assertThatNoException()
            .isThrownBy(
                () -> unitUnderTest.validOrThrow(Instancio.create(TeamBezirkUndWahlIDModel.class)));
      }

      @ParameterizedTest(name = "throw exception when {1}")
      @MethodSource("invalidStimmzettelOwnerModel")
      void should_throwException_when_stimmzettelOwnerIsInvalid(final ArgumentsAccessor arguments) {
        val objectToValidate = arguments.get(0, TeamBezirkUndWahlIDModel.class);

        val mockedWlsException =
            FachlicheWlsException.withCode("000").buildWithMessage("mocked wls exception");
        Mockito.when(
                exceptionFactory.createFachlicheWlsException(
                    ExceptionConstants.STIMMZETTEL_OWNER_IDS_ARE_MISSING))
            .thenReturn(mockedWlsException);

        Assertions.assertThatThrownBy(() -> unitUnderTest.validOrThrow(objectToValidate))
            .usingRecursiveComparison()
            .isEqualTo(mockedWlsException);
      }

      public static Stream<Arguments> invalidStimmzettelOwnerModel() {
        return Stream.of(
            Arguments.of(null, "owner is null"),
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
    class OfBezirkUndWahlID {

      @Test
      void should_notThrowAnyException_when_dataIsCorrect() {
        val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

        Assertions.assertThatNoException()
            .isThrownBy(() -> unitUnderTest.validOrThrow(bezirkUndWahlID));
      }

      @ParameterizedTest(name = "throw exception when {1}")
      @MethodSource("invalidBezirkUndWahlID")
      void should_throwException_when_bezirkUndWahlIDIsInvalid(final ArgumentsAccessor arguments) {
        val objectToValidate = arguments.get(0, BezirkUndWahlID.class);

        val mockedWlsException =
            FachlicheWlsException.withCode("000").buildWithMessage("mocked wls exception");
        Mockito.when(
                exceptionFactory.createFachlicheWlsException(
                    ExceptionConstants.STIMMZETTEL_ANZAHL_IDS_ARE_MISSING))
            .thenReturn(mockedWlsException);

        Assertions.assertThatThrownBy(() -> unitUnderTest.validOrThrow(objectToValidate))
            .usingRecursiveComparison()
            .isEqualTo(mockedWlsException);
      }

      public static Stream<Arguments> invalidBezirkUndWahlID() {
        return Stream.of(
            Arguments.of(null, "owner is null"),
            Arguments.of(
                Instancio.of(BezirkUndWahlID.class)
                    .set(field(BezirkUndWahlID::getWahlbezirkID), null)
                    .create(),
                "wahlbezirkID is null"),
            Arguments.of(
                Instancio.of(BezirkUndWahlID.class)
                    .set(field(BezirkUndWahlID::getWahlbezirkID), "")
                    .create(),
                "wahlbezirkID is empty string"),
            Arguments.of(
                Instancio.of(BezirkUndWahlID.class)
                    .set(field(BezirkUndWahlID::getWahlbezirkID), "   ")
                    .create(),
                "wahlbezirkID is blank string"),
            Arguments.of(
                Instancio.of(BezirkUndWahlID.class)
                    .set(field(BezirkUndWahlID::getWahlID), null)
                    .create(),
                "wahlID is null"),
            Arguments.of(
                Instancio.of(BezirkUndWahlID.class)
                    .set(field(BezirkUndWahlID::getWahlID), "")
                    .create(),
                "wahlID is empty string"),
            Arguments.of(
                Instancio.of(BezirkUndWahlID.class)
                    .set(field(BezirkUndWahlID::getWahlID), "   ")
                    .create(),
                "wahlID is blank string"));
      }
    }
  }
}

package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import static org.instancio.Select.field;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.DataConflictException;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModelValidator;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import java.util.function.Supplier;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StimmzettelValidatorTest {

  @Mock ExceptionFactory exceptionFactory;

  @Mock TeamBezirkUndWahlIDModelValidator teamBezirkUndWahlIDModelValidator;

  @InjectMocks StimmzettelValidator unitUnderTest;

  @Nested
  class ValidOrThrow {

    @Nested
    class OfListStimmzettelOfTeamModel {
      final FachlicheWlsException mockedWlsException =
          FachlicheWlsException.withCode("000").buildWithMessage("mocked wls exception");

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

      @Test
      void should_throwException_when_gueltigkeitIsNull() {
        val invalidStimmzettel =
            Instancio.of(StimmzettelOfTeamModel.class)
                .set(field(StimmzettelOfTeamModel::gueltigkeit), null)
                .create();

        Mockito.when(
                exceptionFactory.createFachlicheWlsException(
                    ExceptionConstants.STIMMZETTEL_GUELTIGKEIT_IS_MISSING))
            .thenReturn(mockedWlsException);

        Assertions.assertThatException()
            .isThrownBy(() -> unitUnderTest.validOrThrow(List.of(invalidStimmzettel)))
            .usingRecursiveComparison()
            .isEqualTo(mockedWlsException);
      }

      @ParameterizedTest(name = "throw exception for wahlvorschlagID when {1}")
      @MethodSource("invalidID")
      void should_throwException_when_wahlvorschlagIDIsInvalid(final ArgumentsAccessor arguments) {
        val wahlvorschlagID = arguments.get(0, String.class);

        val invalidStimmzettel =
            Instancio.of(StimmzettelOfTeamModel.class)
                .set(field(WahlvorschlagModel::wahlvorschlagID), wahlvorschlagID)
                .create();

        Mockito.when(
                exceptionFactory.createFachlicheWlsException(
                    ExceptionConstants.STIMMZETTEL_WAHLVORSCHLAG_ID_IS_MISSING))
            .thenReturn(mockedWlsException);

        Assertions.assertThatException()
            .isThrownBy(() -> unitUnderTest.validOrThrow(List.of(invalidStimmzettel)))
            .usingRecursiveComparison()
            .isEqualTo(mockedWlsException);
      }

      @Test
      void should_throwException_when_wahlvorschlagIsSelectedIsMissing() {
        val invalidStimmzettel =
            Instancio.of(StimmzettelOfTeamModel.class)
                .set(field(WahlvorschlagModel::selected), null)
                .create();

        Mockito.when(
                exceptionFactory.createFachlicheWlsException(
                    ExceptionConstants.STIMMZETTEL_WAHLVORSCHLAG_SELECTED_IS_MISSING))
            .thenReturn(mockedWlsException);

        Assertions.assertThatException()
            .isThrownBy(() -> unitUnderTest.validOrThrow(List.of(invalidStimmzettel)))
            .usingRecursiveComparison()
            .isEqualTo(mockedWlsException);
      }

      @Test
      void should_throwException_when_kandidatIdIsNull() {
        val invalidStimmzettel =
            Instancio.of(StimmzettelOfTeamModel.class).set(field(KandidatModel::id), null).create();

        Mockito.when(
                exceptionFactory.createFachlicheWlsException(
                    ExceptionConstants.STIMMZETTEL_KANDIDAT_ID_IS_MISSING))
            .thenReturn(mockedWlsException);

        Assertions.assertThatException()
            .isThrownBy(() -> unitUnderTest.validOrThrow(List.of(invalidStimmzettel)))
            .usingRecursiveComparison()
            .isEqualTo(mockedWlsException);
      }

      @Test
      void should_throwException_when_kandidatDiscardedIsNull() {
        val invalidStimmzettel =
            Instancio.of(StimmzettelOfTeamModel.class)
                .set(field(KandidatModel::discarded), null)
                .create();

        Mockito.when(
                exceptionFactory.createFachlicheWlsException(
                    ExceptionConstants.STIMMZETTEL_KANDIDAT_DISCARDED_IS_MISSING))
            .thenReturn(mockedWlsException);

        Assertions.assertThatException()
            .isThrownBy(() -> unitUnderTest.validOrThrow(List.of(invalidStimmzettel)))
            .usingRecursiveComparison()
            .isEqualTo(mockedWlsException);
      }

      @ParameterizedTest(name = "throw exception for kandidatID when {1}")
      @MethodSource("invalidID")
      void should_throwException_when_kandidatIdIsInvalid(final ArgumentsAccessor arguments) {
        val kandidatID = arguments.get(0, String.class);

        val invalidStimmzettel =
            Instancio.of(StimmzettelOfTeamModel.class)
                .set(field(KandidatIdModel::kandidatID), kandidatID)
                .create();

        Mockito.when(
                exceptionFactory.createFachlicheWlsException(
                    ExceptionConstants.STIMMZETTEL_KANDIDAT_KANDIDATID_IS_MISSING))
            .thenReturn(mockedWlsException);

        Assertions.assertThatException()
            .isThrownBy(() -> unitUnderTest.validOrThrow(List.of(invalidStimmzettel)))
            .usingRecursiveComparison()
            .isEqualTo(mockedWlsException);
      }

      public static Stream<Arguments> invalidID() {
        return Stream.of(
            Arguments.of(null, "id is null"),
            Arguments.of("", "id is empty string"),
            Arguments.of(" ", "id is blank string"));
      }
    }

    @Nested
    class OfTeamBezirkUndWahlIDModel {

      @Captor ArgumentCaptor<Supplier<WlsException>> captor;

      @Test
      void should_useTeamBezirkUndWahlIDValidator_when_called() {
        val id = Instancio.create(TeamBezirkUndWahlIDModel.class);

        unitUnderTest.validOrThrow(id);

        Mockito.verify(teamBezirkUndWahlIDModelValidator)
            .isValidOrThrow(Mockito.eq(id), captor.capture());
        Assertions.assertThat(captor.getValue().get())
            .isEqualTo(
                exceptionFactory.createFachlicheWlsException(
                    ExceptionConstants.STIMMZETTELERFASSUNG_TEAM_STATUS_INVALID_IDs));
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

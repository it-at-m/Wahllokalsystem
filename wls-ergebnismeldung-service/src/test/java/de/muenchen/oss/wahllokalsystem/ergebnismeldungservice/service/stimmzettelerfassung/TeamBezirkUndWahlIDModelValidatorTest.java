package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung;

import static org.instancio.Select.field;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TeamBezirkUndWahlIDModelValidatorTest {

  TeamBezirkUndWahlIDModelValidator unitUnderTest = new TeamBezirkUndWahlIDModelValidator();

  @Nested
  class IsValidOrThrow {

    private final WlsException MOCKED_SUPPLIED_WLSEXCEPTION =
        FachlicheWlsException.withCode("000").buildWithMessage("mocked wls exception");
    private final Supplier<WlsException> MOCKED_SUPPLIER = () -> MOCKED_SUPPLIED_WLSEXCEPTION;

    @Test
    void should_notThrowAnyException_when_idIsValid() {
      val idToValidate = Instancio.create(TeamBezirkUndWahlIDModel.class);

      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.isValidOrThrow(idToValidate, MOCKED_SUPPLIER));
    }

    @ParameterizedTest(name = "throw exception when {1}")
    @MethodSource("invalidWahlbezirkErfassungsteamID")
    void should_throwException_when_idIsInvalid(final ArgumentsAccessor arguments) {
      val idToValidate = arguments.get(0, TeamBezirkUndWahlIDModel.class);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.isValidOrThrow(idToValidate, MOCKED_SUPPLIER))
          .usingRecursiveComparison()
          .isEqualTo(MOCKED_SUPPLIED_WLSEXCEPTION);
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
}

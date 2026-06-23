package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
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
class BedenklicheStimmzettelValidatorTest {

  @Mock ExceptionFactory exceptionFactory;

  @InjectMocks BedenklicheStimmzettelValidator unitUnderTest;

  @Nested
  class ValidateGetBedenklicheStimmzettelParameterOrThrow {

    @Test
    void should_notThrowAnyException_when_parametersAreValid() {
      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  unitUnderTest.validateGetBedenklicheStimmzettelParameterOrThrow(
                      new BezirkUndWahlID("wahlID", "wahlbezirkID")));
    }

    @ParameterizedTest(name = "provided exception when {1}")
    @MethodSource("invalidWahlbezirkArgumentsWithTestcaseNameAppendix")
    void should_throwProvidedException_when_bezirkUndWahlIdIsNotValid(
        final ArgumentsAccessor arguments) {
      val mockedException =
          FachlicheWlsException.withCode("000").buildWithMessage("mocked fachliche wls exception");
      Mockito.when(
              exceptionFactory.createFachlicheWlsException(
                  ExceptionConstants.GET_BEDENKLICHE_STIMMZETTEL_PARAMETER_UNVOLLSTAENDIG))
          .thenReturn(mockedException);

      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest.validateGetBedenklicheStimmzettelParameterOrThrow(
                      arguments.get(0, BezirkUndWahlID.class)))
          .isSameAs(mockedException);
    }

    public static Stream<Arguments> invalidWahlbezirkArgumentsWithTestcaseNameAppendix() {
      return Stream.of(
          Arguments.of(null, "argument is null"),
          Arguments.of(new BezirkUndWahlID(null, "wahlbezirkID"), "wahlID is null"),
          Arguments.of(new BezirkUndWahlID("", "wahlbezirkID"), "wahlID is empty"),
          Arguments.of(new BezirkUndWahlID("   ", "wahlbezirkID"), "wahlID is blank"),
          Arguments.of(new BezirkUndWahlID("wahlID", null), "wahlbezirkID is null"),
          Arguments.of(new BezirkUndWahlID("wahlID", ""), "wahlbezirkID is is empty"),
          Arguments.of(new BezirkUndWahlID("wahlID", "   "), "wahlbezirkID is blank"));
    }
  }

  @Nested
  class ValidateSetBedenklicheStimmzettelParameterOrThrow {

    @Test
    void should_notThrowAnyException_when_parametersAreValid() {
      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  unitUnderTest.validateSetBedenklicheStimmzettelParameterOrThrow(
                      new BezirkUndWahlID("wahlID", "wahlbezirkID"), Collections.emptyList()));
    }

    @ParameterizedTest(name = "provided exception when {2}")
    @MethodSource("invalidArgumentsWithTestcaseNameAppendix")
    void should_throwProvidedException_when_bezirkUndWahlIdIsNotValid(
        final ArgumentsAccessor arguments) {
      val mockedException =
          FachlicheWlsException.withCode("000").buildWithMessage("mocked fachliche wls exception");
      Mockito.when(
              exceptionFactory.createFachlicheWlsException(
                  ExceptionConstants.POST_BEDENKLICHE_STIMMZETTEL_PARAMETER_UNVOLLSTAENDIG))
          .thenReturn(mockedException);

      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest.validateSetBedenklicheStimmzettelParameterOrThrow(
                      arguments.get(0, BezirkUndWahlID.class), arguments.get(1, Collection.class)))
          .isSameAs(mockedException);
    }

    public static Stream<Arguments> invalidArgumentsWithTestcaseNameAppendix() {
      return Stream.of(
          Arguments.of(null, Collections.emptyList(), "bezirkUndWahlID argument is null"),
          Arguments.of(
              new BezirkUndWahlID("wahlID", "wahlbezirkID"), null, "collection argument is null"),
          Arguments.of(
              new BezirkUndWahlID(null, "wahlbezirkID"), Collections.emptyList(), "wahlID is null"),
          Arguments.of(
              new BezirkUndWahlID("", "wahlbezirkID"), Collections.emptyList(), "wahlID is empty"),
          Arguments.of(
              new BezirkUndWahlID("   ", "wahlbezirkID"),
              Collections.emptyList(),
              "wahlID is blank"),
          Arguments.of(
              new BezirkUndWahlID("wahlID", null), Collections.emptyList(), "wahlbezirkID is null"),
          Arguments.of(
              new BezirkUndWahlID("wahlID", ""),
              Collections.emptyList(),
              "wahlbezirkID is is empty"),
          Arguments.of(
              new BezirkUndWahlID("wahlID", "   "),
              Collections.emptyList(),
              "wahlbezirkID is blank"));
    }
  }
}

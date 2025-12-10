package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahllokalZustand;

import de.muenchen.oss.wahllokalsystem.eaiservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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
class WahllokalZustandValidatorTest {

  @Mock ExceptionFactory exceptionFactory;

  @InjectMocks WahllokalZustandValidator unitUnderTest;

  @Nested
  class ValidWahllokalZustandOrThrow {

    @ParameterizedTest(name = "{1}")
    @MethodSource("minimalValidWahllokalZustandDTOArguments")
    void should_notThrowAnyException_when_wahllokalZustandGivenWithMinimalDataIsValid(
        final ArgumentsAccessor arguments) {
      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  unitUnderTest.validWahllokalZustandOrThrow(
                      arguments.get(0, WahllokalZustandDTO.class)));
    }

    @Test
    void should_throwException_when_argumentIsNull() {
      val mockedWlsException =
          FachlicheWlsException.withCode("000").buildWithMessage("parameter is null");
      Mockito.when(
              exceptionFactory.createFachlicheWlsException(
                  ExceptionConstants.DATENALLGEMEIN_PARAMETER_FEHLEN))
          .thenReturn(mockedWlsException);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.validWahllokalZustandOrThrow(null))
          .isSameAs(mockedWlsException);
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("invalidWahlbezirkIDArguments")
    void should_throwException_when_wahlbezirkIdIsMissing(final ArgumentsAccessor arguments) {
      val mockedWlsException =
          FachlicheWlsException.withCode("000").buildWithMessage("wahlbezirkID is null");
      Mockito.when(
              exceptionFactory.createFachlicheWlsException(
                  ExceptionConstants.SAVEWAHLLOKALZUSTAND_WAHLBEZIRKID_FEHLT))
          .thenReturn(mockedWlsException);

      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest.validWahllokalZustandOrThrow(
                      arguments.get(0, WahllokalZustandDTO.class)))
          .isSameAs(mockedWlsException);
    }

    public static Stream<Arguments> minimalValidWahllokalZustandDTOArguments() {
      return Stream.of(
          Arguments.of(
              new WahllokalZustandDTO("wahlbezirkID", null, null, Collections.emptySet()),
              "Set is empty"),
          Arguments.of(new WahllokalZustandDTO("wahlbezirkID", null, null, null), "Set is null"));
    }

    public static Stream<Arguments> invalidWahlbezirkIDArguments() {
      return Stream.of(
          Arguments.of(
              new WahllokalZustandDTO(null, null, null, Collections.emptySet()),
              "wahlbezirkID is null"),
          Arguments.of(
              new WahllokalZustandDTO("", null, null, Collections.emptySet()),
              "wahlbezirkID is empty string"),
          Arguments.of(
              new WahllokalZustandDTO("   ", null, null, Collections.emptySet()),
              "wahlbezirkID is blank string"));
    }
  }
}

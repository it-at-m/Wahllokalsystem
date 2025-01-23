package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Meldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
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
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WahlUndBezirkIDUndMeldungsartValidatorTest {

    @InjectMocks
    WahlUndBezirkIDUndMeldungsartValidator unitUnderTest;

    @Nested
    class validWahlUndBezirkIDUndMeldungsartOrThrow {

        final FachlicheWlsException providedException = FachlicheWlsException.withCode("").buildWithMessage("sth failed");

        @Test
        void should_notThrowException_when_wahlUndBezirkIDUndMeldungsartIsValid() {
            val id = new WahlUndBezirkIDUndMeldungsart("wahlbezirkID", "wahlID", Meldungsart.V1);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validWahlUndBezirkIDUndMeldungsartOrThrow(id, providedException));
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlUndBezirkIDUndMeldungsartIsNull() {
            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlUndBezirkIDUndMeldungsartOrThrow(null, providedException))
                    .isSameAs(providedException);
        }

        @ParameterizedTest(name = "provided exception when {1}")
        @MethodSource("invalidWahlUndBezirkIDUndMeldungsartArgumentsWithTestcaseNameAppendix")
        void should_throwProvidedException_when_wahlUndBezirkIDIsNotValid(final ArgumentsAccessor arguments) {
            Assertions.assertThatException()
                    .isThrownBy(
                            () -> unitUnderTest.validWahlUndBezirkIDUndMeldungsartOrThrow(arguments.get(0, WahlUndBezirkIDUndMeldungsart.class),
                                    providedException))
                    .isSameAs(providedException);
        }

        public static Stream<Arguments> invalidWahlUndBezirkIDUndMeldungsartArgumentsWithTestcaseNameAppendix() {
            return Stream.of(
                    Arguments.of(new WahlUndBezirkIDUndMeldungsart(null, "wahlID", Meldungsart.V1), "wahlbezirkID is null"),
                    Arguments.of(new WahlUndBezirkIDUndMeldungsart("", "wahlID", Meldungsart.V1), "wahlbezirkID is empty"),
                    Arguments.of(new WahlUndBezirkIDUndMeldungsart("   ", "wahlID", Meldungsart.V1), "wahlbezirkID is blank"),
                    Arguments.of(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID", null, Meldungsart.V1), "wahlID is null"),
                    Arguments.of(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID", "", Meldungsart.V1), "wahlID is is empty"),
                    Arguments.of(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID", "   ", Meldungsart.V1), "wahlID is blank"),
                    Arguments.of(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID", "wahlID", null), "meldungsart is null"));
        }
    }
}

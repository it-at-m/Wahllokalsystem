package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WahlartPredicateHolderTest {

    WahlartPredicateHolder underTest = new WahlartPredicateHolder();

    @Nested
    class GetPredicateForStapelWithInvalidErgebnisse {

        private static final WahlartModel NON_SUPPORTED_WAHLART = WahlartModel.SVW;

        @ParameterizedTest
        @MethodSource("argumentsForSupportedWahlarten")
        void should_returnPredicate_when_anyWahlartExceptSVWIsGiven(final ArgumentsAccessor arguments) {
            val result = underTest.getPredicateForStapelWithInvalidErgebnisse(arguments.get(0, WahlartModel.class));

            Assertions.assertThat(result).isNotNull();
        }

        @ParameterizedTest
        @MethodSource("argumentsForNonSupportedWahlarten")
        void should_throwIllegalArgumentException_when_wahlartIsNotSupported(final ArgumentsAccessor arguments) {
            Assertions.assertThatException().isThrownBy(() -> underTest.getPredicateForStapelWithInvalidErgebnisse(arguments.get(0, WahlartModel.class)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        public static Stream<Arguments> argumentsForSupportedWahlarten() {
            return Arrays.stream(WahlartModel.values())
                    .filter(wahlart -> !NON_SUPPORTED_WAHLART.equals(wahlart))
                    .map(Arguments::of);
        }

        public static Stream<Arguments> argumentsForNonSupportedWahlarten() {
            return Arrays.stream(WahlartModel.values())
                    .filter(NON_SUPPORTED_WAHLART::equals)
                    .map(Arguments::of);
        }
    }

}

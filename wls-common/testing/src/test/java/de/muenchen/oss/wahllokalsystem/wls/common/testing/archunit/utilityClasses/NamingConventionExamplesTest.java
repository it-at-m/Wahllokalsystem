package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

public class NamingConventionExamplesTest {

    @Nested
    public class ExampleTestNamesViolatingNamingConventionRule {

        @Test
        void test() {
        }

        @Test
        void shouldTest123() {
        }

        @Test
        void shouldTest123When456() {
        }

        @Test
        void should_test123When456() {
        }

        @Test
        void shouldTest123_when() {
        }

        @Test
        void should_Test123_When456() {
        }

        @Test
        void shouldTest123_when456() {
        }

        @ParameterizedTest
        @EmptySource
        void should_test_123_when_456_testing(String input) {
        }

        @ParameterizedTest
        @NullSource
        void should_validCamelCase_When_456(String input) {
        }

        @ParameterizedTest
        @ValueSource(ints = { 1, 3, 5 })
        void should_123_when_456() {
        }
    }

    @Nested
    public class ExampleTestNamesFollowingNamingConventionRule {

        @Test
        void should_a_when_b() {
        }

        @Test
        void should_abc_when_def() {
        }

        @Test
        void should_abcDef_when_ghi() {
        }

        @Test
        void should_longTestNameWrittenInCamelCase_when_nameIsStillGoingOn() {
        }

        @ParameterizedTest
        @EmptySource
        void should_beValid_when_havingNumbers123InName(String input) {
        }

        @ParameterizedTest
        @NullSource
        void should_beValid_when_havingMANYCAPITALLETTERS(String input) {
        }

        @ParameterizedTest
        @ValueSource(ints = { 1, 3, 5 })
        void should_beValid_when_endingWithNumbers123() {
        }
    }

    @Nested
    public class ExampleTestNamesWithoutAnnotation {

        void should_abc_when_def() {
        }

        void shouldTest123() {
        }
    }
}

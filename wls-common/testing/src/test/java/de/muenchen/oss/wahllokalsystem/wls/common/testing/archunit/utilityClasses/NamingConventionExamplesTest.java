package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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

        @Test
        void should_test_123_when_456_testing() {
        }

        @Test
        void should_validCamelCase_When_456() {
        }

        @Test
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

        @Test
        void should_beValid_when_havingNumbers123InName() {
        }

        @Test
        void should_beValid_when_havingMANYCAPITALLETTERS() {
        }

        @Test
        void should_beValid_when_endingWithNumbers123() {
        }
    }
}
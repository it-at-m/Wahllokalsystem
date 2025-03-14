package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

public class NamingConventionExamplesTest {

    // Naming Convention for test Names
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
    public class ExampleMethodNamesWithoutAnnotation {

        void should_abc_when_def() {
        }

        void shouldTest123() {
        }
    }

    // Naming Convention for @BeforeEach methods
    @Nested
    public class ExampleBeforeEachMethodNamesViolatingNamingConventionRule {

        @BeforeEach
        void setUp() {
        }

        @BeforeEach
        void set_up() {
        }

        @BeforeEach
        void randomName() {
        }
    }

    @Nested
    public class ExampleBeforeEachMethodNameFollowingNamingConventionRule {

        @BeforeEach
        void setup() {
        }
    }

    // Naming Convention for @AfterEach methods
    @Nested
    public class ExampleAfterEachMethodNamesViolatingNamingConventionRule {

        @AfterEach
        void tearDown() {
        }

        @AfterEach
        void tear_down() {
        }

        @AfterEach
        void randomName() {
        }
    }

    @Nested
    public class ExampleAfterEachMethodNameFollowingNamingConventionRule {

        @AfterEach
        void teardown() {
        }
    }
}

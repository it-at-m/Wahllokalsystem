package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

public class ModifiersExampleTest {

    @Nested
    public class CorrectModifierExamples {

        @Test
        void should_test123_when_test456() {
        }

        @ParameterizedTest
        @NullSource
        void should_execute_when_running(String input) {
        }
    }

    @Nested
    public class WrongModifierExamples {

        @Test
        public void should_abc_when_def() {
        }

        @Test
        protected void should_a123_when_b456() {
        }

        @ParameterizedTest
        @NullSource
        private void should_a_when_b(String input) {
        }
    }
}

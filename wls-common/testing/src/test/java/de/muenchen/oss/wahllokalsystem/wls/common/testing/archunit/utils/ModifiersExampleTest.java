package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utils;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

public class ModifiersExampleTest {

    @Nested
    public class CorrectModifierExamples {

        @Test
        void test123() {
        }

        @ParameterizedTest
        @NullSource
        void test456(String input) {
        }
    }

    @Nested
    public class WrongModifierExamples {

        @Test
        public void test123() {
        }

        @Test
        protected void test456() {
        }

        @ParameterizedTest
        @NullSource
        private void test789(String input) {
        }
    }
}

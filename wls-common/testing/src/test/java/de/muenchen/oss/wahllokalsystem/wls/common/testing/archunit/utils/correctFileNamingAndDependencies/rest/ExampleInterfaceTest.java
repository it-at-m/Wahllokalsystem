package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utils.correctFileNamingAndDependencies.rest;

import org.junit.jupiter.api.Nested;

public class ExampleInterfaceTest {

    @Nested
    class MethodWithOverload {

        @Nested
        class ToString {
        }

        @Nested
        class ToListOfStrings {
        }
    }

    @Nested
    class AnotherMethodWithOverload {

        @Nested
        class ToInteger {
        }

        @Nested
        class ToListOfIntegers {
        }
    }
}

package de.muenchen.oss.wahllokalsystem.infomanagementservice.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.MethodRules;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ArchUnitTest {

    private JavaClasses serviceClasses;

    @BeforeEach
    void setUp() {
        serviceClasses = new ClassFileImporter().importPackages(MicroServiceApplication.class.getPackage().getName());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rulesToVerify")
    void should_verifyArchUnitRuleForAllClassesOfService_when_running(final ArgumentsAccessor arguments) {
        arguments.get(1, ArchRule.class).check(serviceClasses);
    }

    public static Stream<Arguments> rulesToVerify() {
        return Stream.of(
                Arguments.of("TEST_NAMING_CONVENTION_RULE", MethodRules.RULE_TEST_NAMING_CONVENTION_SHOULD_WHEN_MATCHED));
    }
}

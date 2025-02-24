package de.muenchen.oss.wahllokalsystem.basisdatenservice.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.elements.MethodsShouldConjunction;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ArchUnitTest {

    private static final String REGEX_TEST_CASE_NAME = "^should_([a-z]+([A-Z]+[a-z]*)+)_when_([a-z]+([A-Z]+[a-z]*)+)$";

    private static final MethodsShouldConjunction RULE_TEST_NAMING_CONVENTION_SHOULD_WHEN_MATCHED = methods()
            .that().areAnnotatedWith(Test.class)
            .should().haveNameMatching(REGEX_TEST_CASE_NAME);

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
                Arguments.of("TEST_NAMING_CONVENTION_RULE", RULE_TEST_NAMING_CONVENTION_SHOULD_WHEN_MATCHED));
    }
}

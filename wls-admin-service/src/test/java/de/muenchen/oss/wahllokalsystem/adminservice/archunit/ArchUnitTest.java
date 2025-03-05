package de.muenchen.oss.wahllokalsystem.adminservice.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.elements.MethodsShouldConjunction;
import de.muenchen.oss.wahllokalsystem.adminservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.ClassRules;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ArchUnitTest {

    private static final String REGEX_TEST_CASE_NAME = "should_[a-z].*_when_[a-z].*";

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
                Arguments.of("TEST_NAMING_CONVENTION_RULE", RULE_TEST_NAMING_CONVENTION_SHOULD_WHEN_MATCHED),
                Arguments.of("RULE_DATAMODEL_IN_REST_ENDS_WITH_DTO_CONVENTION_MATCHED", ClassRules.RULE_DATAMODEL_IN_REST_ENDS_WITH_DTO_CONVENTION_MATCHED),
                Arguments.of("RULE_DATAMODEL_IN_SERVICE_ENDS_WITH_Model_CONVENTION_MATCHED",
                        ClassRules.RULE_DATAMODEL_IN_SERVICE_ENDS_WITH_Model_CONVENTION_MATCHED),
                Arguments.of("RULE_DATAMODEL_IN_DOMAIN_HAS_NO_ENDING_CONVENTION_MATCHED",
                        ClassRules.RULE_DATAMODEL_IN_DOMAIN_HAS_NO_ENDING_CONVENTION_MATCHED.allowEmptyShould(true)),
                Arguments.of("RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_REST_CONVENTION_MATCHED",
                        ClassRules.RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_REST_CONVENTION_MATCHED),
                Arguments.of("RULE_NO_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED",
                        ClassRules.RULE_NO_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED),
                Arguments.of("RULE_NO_CROSS_DEPENDENCIES_INSIDE_DOMAIN_CONVENTION_MATCHED",
                        ClassRules.RULE_NO_CROSS_DEPENDENCIES_INSIDE_DOMAIN_CONVENTION_MATCHED.allowEmptyShould(true))
        );
    }
}

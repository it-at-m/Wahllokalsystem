package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.ClassRules.RULE_DATAMODEL_IN_DOMAIN_HAS_NO_ENDING_CONVENTION_MATCHED;
import static de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.ClassRules.RULE_DATAMODEL_IN_REST_ENDS_WITH_DTO_CONVENTION_MATCHED;
import static de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.ClassRules.RULE_DATAMODEL_IN_SERVICE_ENDS_WITH_Model_CONVENTION_MATCHED;
import static de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.ClassRules.RULE_NO_CROSS_DEPENDENCIES_INSIDE_DOMAIN_CONVENTION_MATCHED;
import static de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.ClassRules.RULE_NO_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED;
import static de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.ClassRules.RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_REST_CONVENTION_MATCHED;
import static de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.ClassRules.RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED;

public class ClassRulesTest {

    static String INCORRECT_FILENAMING_AND_DEPENDENCIES_PACKAGE_PATH = "de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.incorrectFileNamingAndDependencies";
    static String CORRECT_FILENAMING_AND_DEPENDENCIES_PACKAGE_PATH = "de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.correctFileNamingAndDependencies";

    @ParameterizedTest
    @MethodSource("getRulesAndMatchingTestClassesThrowingErrors")
    void should_throwError_when_ruleNotSatisfied(ArchRule ruleUnderTest, String packageString) {
        JavaClasses classes = new ClassFileImporter().importPackages(packageString);

        Assertions.assertThatThrownBy(() -> ruleUnderTest.check(classes)).isInstanceOf(AssertionError.class);
    }

    @ParameterizedTest
    @MethodSource("getRulesAndMatchingTestClassesNotThrowingErrors")
    void should_throwNoError_when_ruleSatisfied(ArchRule ruleUnderTest, String packageString) {
        JavaClasses classes = new ClassFileImporter().importPackages(packageString);

        Assertions.assertThatCode(() -> ruleUnderTest.check(classes)).doesNotThrowAnyException();
    }

    private static Stream<Arguments> getRulesAndMatchingTestClassesThrowingErrors() {
        return Stream.of(
                Arguments.of(RULE_DATAMODEL_IN_REST_ENDS_WITH_DTO_CONVENTION_MATCHED,
                        INCORRECT_FILENAMING_AND_DEPENDENCIES_PACKAGE_PATH + ".rest"),
                Arguments.of(RULE_DATAMODEL_IN_SERVICE_ENDS_WITH_Model_CONVENTION_MATCHED,
                        INCORRECT_FILENAMING_AND_DEPENDENCIES_PACKAGE_PATH + ".service"),
                Arguments.of(RULE_DATAMODEL_IN_DOMAIN_HAS_NO_ENDING_CONVENTION_MATCHED,
                        INCORRECT_FILENAMING_AND_DEPENDENCIES_PACKAGE_PATH + ".domain"),
                Arguments.of(RULE_NO_CROSS_DEPENDENCIES_INSIDE_DOMAIN_CONVENTION_MATCHED,
                        INCORRECT_FILENAMING_AND_DEPENDENCIES_PACKAGE_PATH),
                Arguments.of(RULE_NO_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED,
                        INCORRECT_FILENAMING_AND_DEPENDENCIES_PACKAGE_PATH),
                Arguments.of(RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_REST_CONVENTION_MATCHED,
                        INCORRECT_FILENAMING_AND_DEPENDENCIES_PACKAGE_PATH),
                Arguments.of(RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED,
                        INCORRECT_FILENAMING_AND_DEPENDENCIES_PACKAGE_PATH));
    }

    private static Stream<Arguments> getRulesAndMatchingTestClassesNotThrowingErrors() {
        return Stream.of(
                Arguments.of(RULE_DATAMODEL_IN_REST_ENDS_WITH_DTO_CONVENTION_MATCHED,
                        CORRECT_FILENAMING_AND_DEPENDENCIES_PACKAGE_PATH + ".rest"),
                Arguments.of(RULE_DATAMODEL_IN_SERVICE_ENDS_WITH_Model_CONVENTION_MATCHED,
                        CORRECT_FILENAMING_AND_DEPENDENCIES_PACKAGE_PATH + ".service"),
                Arguments.of(RULE_DATAMODEL_IN_DOMAIN_HAS_NO_ENDING_CONVENTION_MATCHED,
                        CORRECT_FILENAMING_AND_DEPENDENCIES_PACKAGE_PATH + ".domain"),
                Arguments.of(RULE_NO_CROSS_DEPENDENCIES_INSIDE_DOMAIN_CONVENTION_MATCHED,
                        CORRECT_FILENAMING_AND_DEPENDENCIES_PACKAGE_PATH),
                Arguments.of(RULE_NO_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED,
                        CORRECT_FILENAMING_AND_DEPENDENCIES_PACKAGE_PATH),
                Arguments.of(RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_REST_CONVENTION_MATCHED,
                        CORRECT_FILENAMING_AND_DEPENDENCIES_PACKAGE_PATH),
                Arguments.of(RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED,
                        CORRECT_FILENAMING_AND_DEPENDENCIES_PACKAGE_PATH));
    }
}

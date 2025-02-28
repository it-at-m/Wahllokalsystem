package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.ClassRules.RULE_DATAMODEL_IN_REST_ENDS_WITH_DTO_CONVENTION_MATCHED;

public class ClassRulesTest {

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
                        "de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.incorrectFileNaming.rest"));
    }

    private static Stream<Arguments> getRulesAndMatchingTestClassesNotThrowingErrors() {
        return Stream.of(
                Arguments.of(RULE_DATAMODEL_IN_REST_ENDS_WITH_DTO_CONVENTION_MATCHED,
                        "de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.correctFileNaming.rest"));
    }
}

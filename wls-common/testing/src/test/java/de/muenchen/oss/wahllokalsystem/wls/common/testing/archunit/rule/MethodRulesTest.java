package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.NamingConventionExamplesTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.MethodRules.RULE_AFTER_EACH_NAMING_CONVENTION_MATCHED;
import static de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.MethodRules.RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED;
import static de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.MethodRules.RULE_TEST_NAMING_CONVENTION_SHOULD_WHEN_MATCHED;

public class MethodRulesTest {

    @ParameterizedTest
    @MethodSource("getRulesAndMatchingTestClassesThrowingErrors")
    void should_throwError_when_ruleNotSatisfied(ArchRule ruleUnderTest, Class<?> testClass) {
        Assertions.assertThatThrownBy(() -> ruleUnderTest.check(new ClassFileImporter()
                .importClasses(testClass)))
                .isInstanceOf(AssertionError.class)
                .satisfies(assertionError -> Assertions.assertThat(assertionError.getMessage().split(System.lineSeparator()).length)
                        .isGreaterThanOrEqualTo(testClass.getMethods().length));
    }

    @ParameterizedTest
    @MethodSource("getRulesAndTestClassesWithoutMatchingCasesThrowingErrors")
    void should_throwError_when_noCasesMatchingRuleWereFound(ArchRule ruleUnderTest, Class<?> testClass) {
        Assertions.assertThatCode(() -> ruleUnderTest.check(new ClassFileImporter()
                .importClasses(testClass)))
                .isInstanceOf(AssertionError.class)
                .satisfies(assertionError -> Assertions.assertThat(assertionError.getMessage())
                        .contains("failed to check any classes"));
    }

    @ParameterizedTest
    @MethodSource("getRulesAndMatchingTestClassesNotThrowingErrors")
    void should_throwNoError_when_ruleSatisfied(ArchRule ruleUnderTest, Class<?> testClass) {
        Assertions.assertThatCode(() -> ruleUnderTest.check(new ClassFileImporter()
                .importClasses(testClass)))
                .doesNotThrowAnyException();
    }

    private static Stream<Arguments> getRulesAndMatchingTestClassesThrowingErrors() {
        return Stream.of(
                Arguments.of(RULE_TEST_NAMING_CONVENTION_SHOULD_WHEN_MATCHED,
                        NamingConventionExamplesTest.ExampleTestNamesViolatingNamingConventionRule.class),
                Arguments.of(RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED,
                        NamingConventionExamplesTest.ExampleBeforeEachMethodNamesViolatingNamingConventionRule.class),
                Arguments.of(RULE_AFTER_EACH_NAMING_CONVENTION_MATCHED,
                        NamingConventionExamplesTest.ExampleAfterEachMethodNamesViolatingNamingConventionRule.class));
    }

    private static Stream<Arguments> getRulesAndTestClassesWithoutMatchingCasesThrowingErrors() {
        return Stream.of(
                Arguments.of(RULE_TEST_NAMING_CONVENTION_SHOULD_WHEN_MATCHED,
                        NamingConventionExamplesTest.ExampleTestNamesWithoutAnntation.class),
                Arguments.of(RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED,
                        NamingConventionExamplesTest.ExampleTestNamesWithoutAnntation.class),
                Arguments.of(RULE_AFTER_EACH_NAMING_CONVENTION_MATCHED,
                        NamingConventionExamplesTest.ExampleTestNamesWithoutAnntation.class));
    }

    private static Stream<Arguments> getRulesAndMatchingTestClassesNotThrowingErrors() {
        return Stream.of(
                Arguments.of(RULE_TEST_NAMING_CONVENTION_SHOULD_WHEN_MATCHED,
                        NamingConventionExamplesTest.ExampleTestNamesFollowingNamingConventionRule.class),
                Arguments.of(RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED,
                        NamingConventionExamplesTest.ExampleBeforeEachMethodNameFollowingNamingConventionRule.class),
                Arguments.of(RULE_AFTER_EACH_NAMING_CONVENTION_MATCHED,
                        NamingConventionExamplesTest.ExampleAfterEachMethodNameFollowingNamingConventionRule.class));
    }
}

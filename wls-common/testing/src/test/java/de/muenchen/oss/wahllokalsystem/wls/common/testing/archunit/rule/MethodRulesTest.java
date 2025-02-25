package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.NamingConventionExamplesTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.MethodRules.RULE_AFTER_EACH_NAMING_CONVENTION_MATCHED;
import static de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.MethodRules.RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED;
import static de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.MethodRules.RULE_TEST_NAMING_CONVENTION_SHOULD_WHEN_MATCHED;

public class MethodRulesTest {

    @Nested
    class TestNamingConventionRule {

        final ArchRule ruleUnderTest = RULE_TEST_NAMING_CONVENTION_SHOULD_WHEN_MATCHED;

        @Test
        void should_throwError_when_ruleNotMatching() {
            Assertions.assertThatThrownBy(() -> ruleUnderTest.check(new ClassFileImporter()
                    .importClasses(NamingConventionExamplesTest.ExampleTestNamesViolatingNamingConventionRule.class)))
                    .isInstanceOf(AssertionError.class);
        }

        @Test
        void should_throwNoError_when_ruleMatching() {
            Assertions.assertThatCode(() -> ruleUnderTest.check(new ClassFileImporter()
                    .importClasses(NamingConventionExamplesTest.ExampleTestNamesFollowingNamingConventionRule.class)))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class BeforeEachNamingConventionRule {

        final ArchRule ruleUnderTest = RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED;

        @Test
        void should_throwError_when_ruleNotMatching() {
            Assertions.assertThatThrownBy(() -> ruleUnderTest.check(new ClassFileImporter()
                    .importClasses(NamingConventionExamplesTest.ExampleBeforeEachMethodNamesViolatingNamingConventionRule.class)))
                    .isInstanceOf(AssertionError.class);
        }

        @Test
        void should_throwNoError_when_ruleMatching() {
            Assertions.assertThatCode(() -> ruleUnderTest.check(new ClassFileImporter()
                    .importClasses(NamingConventionExamplesTest.ExampleBeforeEachMethodNameFollowingNamingConventionRule.class)))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class AfterEachNamingConventionRule {

        final ArchRule ruleUnderTest = RULE_AFTER_EACH_NAMING_CONVENTION_MATCHED;

        @Test
        void should_throwError_when_ruleNotMatching() {
            Assertions.assertThatThrownBy(() -> ruleUnderTest.check(new ClassFileImporter()
                    .importClasses(NamingConventionExamplesTest.ExampleAfterEachMethodNamesViolatingNamingConventionRule.class)))
                    .isInstanceOf(AssertionError.class);
        }

        @Test
        void should_throwNoError_when_ruleMatching() {
            Assertions.assertThatCode(() -> ruleUnderTest.check(new ClassFileImporter()
                    .importClasses(NamingConventionExamplesTest.ExampleAfterEachMethodNameFollowingNamingConventionRule.class)))
                    .doesNotThrowAnyException();
        }
    }
}

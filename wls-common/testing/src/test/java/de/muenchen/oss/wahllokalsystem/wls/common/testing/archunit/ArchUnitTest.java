package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.equivalentTo;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.MethodRules;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utils.ModifiersExampleTest;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utils.NamingConventionExamplesTest;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utils.incorrectFilenamingAndDependencies.test.TestWithWrongEnding;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ArchUnitTest {

    private static JavaClasses allTestClassesNamingConvention;
    private static JavaClasses allTestClassesBeforeEachConvention;
    private static JavaClasses allTestClassesAfterEachConvention;
    private static JavaClasses allTestClassesTestMethodsArePrivatePackageConvention;
    private static JavaClasses allTestClassesEndWithTestConvention;

    @BeforeAll
    static void init() {
        allTestClassesNamingConvention = new ClassFileImporter()
                .withImportOption(new ImportOption.OnlyIncludeTests())
                .importPackages(SecurityUtils.class.getPackage().getName())
                // excluding intended negative examples from actual test
                .that(not(equivalentTo(NamingConventionExamplesTest.ExampleTestNamesViolatingNamingConventionRule.class)));

        allTestClassesBeforeEachConvention = new ClassFileImporter()
                .withImportOption(new ImportOption.OnlyIncludeTests())
                .importPackages(SecurityUtils.class.getPackage().getName())
                // excluding intended negative examples from actual test
                .that(not(equivalentTo(NamingConventionExamplesTest.ExampleBeforeEachMethodNamesViolatingNamingConventionRule.class)));

        allTestClassesAfterEachConvention = new ClassFileImporter()
                .withImportOption(new ImportOption.OnlyIncludeTests())
                .importPackages(SecurityUtils.class.getPackage().getName())
                // excluding intended negative examples from actual test
                .that(not(equivalentTo(NamingConventionExamplesTest.ExampleAfterEachMethodNamesViolatingNamingConventionRule.class)));

        allTestClassesTestMethodsArePrivatePackageConvention = new ClassFileImporter()
                .withImportOption(new ImportOption.OnlyIncludeTests())
                .importPackages(SecurityUtils.class.getPackage().getName())
                // excluding intended negative examples from actual test
                .that(not(equivalentTo(ModifiersExampleTest.WrongModifierExamples.class)));

        allTestClassesEndWithTestConvention = new ClassFileImporter()
                .importPackages(SecurityUtils.class.getPackage().getName())
                // excluding intended negative examples from actual test
                .that(not(equivalentTo(TestWithWrongEnding.class)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allTestClassesRulesToVerify")
    void should_verifyArchUnitRuleForAllTestClassesOfService_when_running(final ArgumentsAccessor arguments) {
        arguments.get(1, ArchRule.class).check(arguments.get(2, JavaClasses.class));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allClassesRulesToVerify")
    void should_verifyArchUnitRuleForAllClassesOfService_when_running(final ArgumentsAccessor arguments) {
        arguments.get(1, ArchRule.class).check(allTestClassesEndWithTestConvention);
    }

    public static Stream<Arguments> allTestClassesRulesToVerify() {
        return Stream.of(
                Arguments.of(
                        "TEST_NAMING_CONVENTION_RULE",
                        MethodRules.RULE_TEST_NAMING_CONVENTION_SHOULD_WHEN_MATCHED,
                        allTestClassesNamingConvention),
                Arguments.of(
                        "RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED",
                        MethodRules.RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED,
                        allTestClassesBeforeEachConvention),
                Arguments.of(
                        "RULE_AFTER_EACH_NAMING_CONVENTION_MATCHED",
                        MethodRules.RULE_AFTER_EACH_NAMING_CONVENTION_MATCHED,
                        allTestClassesAfterEachConvention),
                Arguments.of("TEST_METHODS_ARE_PACKAGE_PRIVATE_CONVENTION_MATCHED",
                        MethodRules.RULE_TEST_METHODS_ARE_PACKAGE_PRIVATE_CONVENTION_MATCHED,
                        allTestClassesTestMethodsArePrivatePackageConvention));
    }

    private static Stream<Arguments> allClassesRulesToVerify() {
        return Stream.of(
                Arguments.of("RULE_TESTCLASSES_END_WITH_TEST_CONVENTION_MATCHED",
                        MethodRules.RULE_TESTCLASSES_END_WITH_TEST_CONVENTION_MATCHED,
                        allTestClassesEndWithTestConvention));
    }
}

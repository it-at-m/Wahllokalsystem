package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.condition.NestedTestsuitesHaveMatchingMethodCondition;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.MethodRules;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ArchUnitTest {

    private static JavaClasses allTestClasses;
    private static JavaClasses allServiceClasses;

    @BeforeAll
    static void init() {
        allTestClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.OnlyIncludeTests())
                .importPackages(MicroServiceApplication.class.getPackage().getName());

        allServiceClasses = new ClassFileImporter()
                .importPackages(MicroServiceApplication.class.getPackage().getName());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allTestClassesRulesToVerify")
    void should_verifyArchUnitRuleForAllTestClassesOfService_when_running(final ArgumentsAccessor arguments) {
        arguments.get(1, ArchRule.class).check(allTestClasses);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allServiceClassesRulesToVerify")
    void should_verifyArchUnitRuleForAllClassesOfService_when_running(final ArgumentsAccessor arguments) {
        arguments.get(1, ArchRule.class).check(allServiceClasses);
    }

    public static Stream<Arguments> allTestClassesRulesToVerify() {
        return Stream.of(
                Arguments.of("RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED", MethodRules.RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED),
                Arguments.of("RULE_AFTER_EACH_NAMING_CONVENTION_MATCHED", MethodRules.RULE_AFTER_EACH_NAMING_CONVENTION_MATCHED),
                Arguments.of("TEST_METHODS_ARE_PACKAGE_PRIVATE_CONVENTION_MATCHED", MethodRules.RULE_TEST_METHODS_ARE_PACKAGE_PRIVATE_CONVENTION_MATCHED));
    }

    private static Stream<Arguments> allServiceClassesRulesToVerify() {
        return Stream.of(
                Arguments.of("RULE_NESTED_TESTSUITE_HAS_CORRESPONDING_PUBLIC_METHOD_CONVENTION_MATCHED",
                        RULE_NESTED_TESTSUITE_HAS_CORRESPONDING_PUBLIC_METHOD_CONVENTION_MATCHED));
    }

    // override RULE_NESTED_TESTSUITE_HAS_CORRESPONDING_PUBLIC_METHOD_CONVENTION_MATCHED to add custom TestNames to excluded Files
    // TODO use rule from wls-common with default excluded files, when Issue #1014 is done
    private static final ArchRule RULE_NESTED_TESTSUITE_HAS_CORRESPONDING_PUBLIC_METHOD_CONVENTION_MATCHED = classes()
            .that().areAnnotatedWith(Nested.class).should(new NestedTestsuitesHaveMatchingMethodCondition(Set.of(
                    "IntegrationTest", "ConfigurationTest", "ServiceSecurityTest", "ArchUnitTest", "ExceptionFactoryTest")));
}

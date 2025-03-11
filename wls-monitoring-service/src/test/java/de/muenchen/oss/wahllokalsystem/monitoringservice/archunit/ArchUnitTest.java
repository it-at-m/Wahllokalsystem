package de.muenchen.oss.wahllokalsystem.monitoringservice.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import de.muenchen.oss.wahllokalsystem.monitoringservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.MethodRules;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ArchUnitTest {

    private static JavaClasses allTestClasses;

    @BeforeAll
    static void init() {
        allTestClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.OnlyIncludeTests())
                .importPackages(MicroServiceApplication.class.getPackage().getName());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allTestClassesRulesToVerify")
    void should_verifyArchUnitRuleForAllTestClassesOfService_when_running(final ArgumentsAccessor arguments) {
        arguments.get(1, ArchRule.class).check(allTestClasses);
    }

    public static Stream<Arguments> allTestClassesRulesToVerify() {
        return Stream.of(
                Arguments.of("TEST_NAMING_CONVENTION_RULE", MethodRules.RULE_TEST_NAMING_CONVENTION_SHOULD_WHEN_MATCHED),
                Arguments.of("RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED", MethodRules.RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED),
                Arguments.of("RULE_AFTER_EACH_NAMING_CONVENTION_MATCHED", MethodRules.RULE_AFTER_EACH_NAMING_CONVENTION_MATCHED));
    }
}

package de.muenchen.oss.wahllokalsystem.wls.common.exception.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
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
                .importPackages(FachlicheWlsException.class.getPackage().getName());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allServiceClassesRulesToVerify")
    void should_verifyArchUnitRuleForAllClassesOfService_when_running(final ArgumentsAccessor arguments) {
        arguments.get(1, ArchRule.class).check(allTestClasses);
    }

    public static Stream<Arguments> allServiceClassesRulesToVerify() {
        return Stream.of(Arguments.of("RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED", MethodRules.RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED));
    }
}

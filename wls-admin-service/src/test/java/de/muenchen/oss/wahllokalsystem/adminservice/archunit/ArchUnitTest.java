package de.muenchen.oss.wahllokalsystem.adminservice.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import de.muenchen.oss.wahllokalsystem.adminservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.ClassRules;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.MethodRules;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ArchUnitTest {

    private static JavaClasses allTestClasses;
    private static JavaClasses allClassesWithoutTests;
    private final static ImportOption ignoreGeneratedCode = location -> !location.contains("/eai");

    @BeforeAll
    static void init() {
        allTestClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.OnlyIncludeTests())
                .importPackages(MicroServiceApplication.class.getPackage().getName());

        allClassesWithoutTests = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .withImportOption(ignoreGeneratedCode)
                .importPackages(MicroServiceApplication.class.getPackage().getName());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allTestClassesRulesToVerify")
    void should_verifyArchUnitRuleForAllTestClassesOfService_when_running(final ArgumentsAccessor arguments) {
        arguments.get(1, ArchRule.class).check(allTestClasses);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allClassesOfRestRulesToVerify")
    void should_verifyArchUnitRuleForAllClassesOfRest_when_running(final ArgumentsAccessor arguments) {
        arguments.get(1, ArchRule.class).check(allClassesWithoutTests);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allClassesOfServiceRulesToVerify")
    void should_verifyArchUnitRuleForAllClassesOfService_when_running(final ArgumentsAccessor arguments) {
        arguments.get(1, ArchRule.class).check(allClassesWithoutTests);
    }

    public static Stream<Arguments> allTestClassesRulesToVerify() {
        return Stream.of(
                Arguments.of("TEST_NAMING_CONVENTION_RULE", MethodRules.RULE_TEST_NAMING_CONVENTION_SHOULD_WHEN_MATCHED),
                Arguments.of("RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED", MethodRules.RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED));
    }

    public static Stream<Arguments> allClassesOfRestRulesToVerify() {
        return Stream.of(
                Arguments.of("DATAMODEL_IN_REST_ENDS_WITH_DTO_CONVENTION_MATCHED", ClassRules.RULE_DATAMODEL_IN_REST_ENDS_WITH_DTO_CONVENTION_MATCHED),
                Arguments.of("RULE_NO_DTOS_OR_CONTROLLERS_OUTSIDE_OF_REST_PACKAGE_CONVENTION_MATCHED",
                        ClassRules.RULE_NO_DTOS_OR_CONTROLLERS_OUTSIDE_OF_REST_PACKAGE_CONVENTION_MATCHED),
                Arguments.of("RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_REST_CONVENTION_MATCHED",
                        ClassRules.RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_REST_CONVENTION_MATCHED),
                Arguments.of("RULE_NO_CROSS_DEPENDENCIES_INSIDE_REST_CONVENTION_MATCHED",
                        ClassRules.RULE_NO_CROSS_DEPENDENCIES_INSIDE_REST_CONVENTION_MATCHED));
    }

    public static Stream<Arguments> allClassesOfServiceRulesToVerify() {
        return Stream.of(
                Arguments.of("RULE_DATAMODEL_IN_SERVICE_ENDS_WITH_MODEL_CONVENTION_MATCHED",
                        ClassRules.RULE_DATAMODEL_IN_SERVICE_ENDS_WITH_MODEL_CONVENTION_MATCHED),
                Arguments.of("RULE_NO_MODELS_OR_SERVICES_OUTSIDE_OF_SERVICE_PACKAGE_CONVENTION_MATCHED",
                        ClassRules.RULE_NO_MODELS_OR_SERVICES_OUTSIDE_OF_SERVICE_PACKAGE_CONVENTION_MATCHED),
                Arguments.of("RULE_NO_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED",
                        ClassRules.RULE_NO_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED),
                Arguments.of("RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED",
                        ClassRules.RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED));
    }
}

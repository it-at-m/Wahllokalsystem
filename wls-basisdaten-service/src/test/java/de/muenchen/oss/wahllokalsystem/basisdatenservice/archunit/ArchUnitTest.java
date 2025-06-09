package de.muenchen.oss.wahllokalsystem.basisdatenservice.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.ClassRules;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule.MethodRules;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.util.SimpleMethodInvocation;

@Slf4j
public class ArchUnitTest {

    private static JavaClasses allTestClasses;
    private static JavaClasses allClasses;
    private static JavaClasses allClassesWithoutTests;
    private static final ImportOption ignoreGeneratedCode = location -> !location.contains("/eai");
    private static final ImportOption ignoreGeneratedCodeWlsCommon = location -> !location.contains("/common");
    private static final ArchRule RULE_NO_CROSS_DEPENDENCIES_INSIDE_REST_CONVENTION_MATCHED = noClasses()
            .that().resideInAnyPackage("..rest..")
            .should().dependOnClassesThat().resideInAnyPackage("..basisdatenservice.domain..");
    private static final ArchRule RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED = noClasses()
            .that().resideInAnyPackage("..service..").and().haveSimpleNameEndingWith("Model")
            .should().dependOnClassesThat().resideInAnyPackage("..rest..", "..basisdaten.domain..");

    @BeforeAll
    static void init() {
        allTestClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.OnlyIncludeTests())
                .importPackages(MicroServiceApplication.class.getPackage().getName());

        allClasses = new ClassFileImporter()
                .importPackages(MicroServiceApplication.class.getPackage().getName());

        allClassesWithoutTests = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .withImportOption(ignoreGeneratedCode)
                .withImportOption(ignoreGeneratedCodeWlsCommon)
                .importPackages(MicroServiceApplication.class.getPackage().getName());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allTestClassesRulesToVerify")
    void should_verifyArchUnitRuleForAllTestClassesOfService_when_running(final ArgumentsAccessor arguments) {
        arguments.get(1, ArchRule.class).check(allTestClasses);
    }

    @Test
    void should_detectAuthorities_when_running() {

        Authentication authentication = new UsernamePasswordAuthenticationToken("user", "password",
                List.of(new SimpleGrantedAuthority("Basisdaten_BUSINESSACTION_PostUngueltigews")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        StandardEvaluationContext context = new StandardEvaluationContext(authentication);
        //        context.setRootObject(new MethodSecurityExpressionRoot(authentication));
        //        ((StandardEvaluationContext) context).setRootObject(new SecurityExpressionRoot(authentication));

        val handler = new DefaultMethodSecurityExpressionHandler();
        val ctx = handler.createEvaluationContext(authentication,
                new SimpleMethodInvocation(new ArchUnitTest(), ArchUnitTest.class.getDeclaredMethods()[0]));

        Expression expression = new SpelExpressionParser().parseExpression(
                "hasAuthority('Basisdaten_BUSINESSACTION_PostUngueltigews') and hasAuthority('Basisdaten_BUSINESSACTION_PostUngueltigews')");
        log.info("{}", expression.getValue(ctx));

        val chainDetector = new DetectAuthorityChain();
        methods().that(new MethodWithSecurityPredicate()).should(chainDetector).check(allClasses);

        log.info("------------------------------------");
        log.info("- Result                           -");
        log.info("------------------------------------");

        chainDetector.getMethodCalls().forEach(((javaMethod, javaMethodCalls) -> {
            log.info("Method: {}", javaMethod.getFullName());
            val preAuthorizeAnnotation = javaMethod.getAnnotationOfType(PreAuthorize.class);
            log.info("PreAuthorize: {}", preAuthorizeAnnotation.value());
            javaMethodCalls.forEach(javaMethodCall -> log.info("  -> {}", javaMethodCall.getFullName()));
            log.info("------------------------------------------------------------");
        }));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allClassesRulesToVerify")
    void should_verifyArchUnitRuleForAllClasses_when_running(final ArgumentsAccessor arguments) {
        arguments.get(1, ArchRule.class).check(allClasses);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allClassesWithoutTestsRulesToVerify")
    void should_verifyArchUnitRuleForAllClassesWithoutTests_when_running(final ArgumentsAccessor arguments) {
        arguments.get(1, ArchRule.class).check(allClassesWithoutTests);
    }

    public static Stream<Arguments> allTestClassesRulesToVerify() {
        return Stream.of(
                Arguments.of("TEST_NAMING_CONVENTION_RULE", MethodRules.RULE_TEST_NAMING_CONVENTION_SHOULD_WHEN_MATCHED),
                Arguments.of("RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED", MethodRules.RULE_BEFORE_EACH_NAMING_CONVENTION_MATCHED),
                Arguments.of("RULE_AFTER_EACH_NAMING_CONVENTION_MATCHED", MethodRules.RULE_AFTER_EACH_NAMING_CONVENTION_MATCHED),
                Arguments.of("TEST_METHODS_ARE_PACKAGE_PRIVATE_CONVENTION_MATCHED", MethodRules.RULE_TEST_METHODS_ARE_PACKAGE_PRIVATE_CONVENTION_MATCHED));
    }

    private static Stream<Arguments> allClassesRulesToVerify() {
        return Stream.of(
                Arguments.of("RULE_NESTED_TESTSUITE_HAS_CORRESPONDING_PUBLIC_METHOD_CONVENTION_MATCHED",
                        ClassRules.RULE_NESTED_TESTSUITE_HAS_CORRESPONDING_PUBLIC_METHOD_CONVENTION_MATCHED),
                Arguments.of("RULE_TESTCLASSES_END_WITH_TEST_CONVENTION_MATCHED",
                        MethodRules.RULE_TESTCLASSES_END_WITH_TEST_CONVENTION_MATCHED));
    }

    private static Stream<Arguments> allClassesWithoutTestsRulesToVerify() {
        return Stream.of(
                //--- rest rules
                Arguments.of("DATAMODEL_IN_REST_ENDS_WITH_DTO_CONVENTION_MATCHED",
                        ClassRules.RULE_DATAMODEL_IN_REST_ENDS_WITH_DTO_CONVENTION_MATCHED),
                Arguments.of("RULE_NO_DTOS_OR_CONTROLLERS_OUTSIDE_OF_REST_PACKAGE_CONVENTION_MATCHED",
                        ClassRules.RULE_NO_DTOS_OR_CONTROLLERS_OUTSIDE_OF_REST_PACKAGE_CONVENTION_MATCHED),
                Arguments.of("RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_REST_CONVENTION_MATCHED",
                        ClassRules.RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_REST_CONVENTION_MATCHED),
                Arguments.of("RULE_NO_CROSS_DEPENDENCIES_INSIDE_REST_CONVENTION_MATCHED",
                        RULE_NO_CROSS_DEPENDENCIES_INSIDE_REST_CONVENTION_MATCHED),
                //--- service rules
                Arguments.of("RULE_DATAMODEL_IN_SERVICE_ENDS_WITH_MODEL_CONVENTION_MATCHED",
                        ClassRules.RULE_DATAMODEL_IN_SERVICE_ENDS_WITH_MODEL_CONVENTION_MATCHED),
                Arguments.of("RULE_NO_MODELS_OR_SERVICES_OUTSIDE_OF_SERVICE_PACKAGE_CONVENTION_MATCHED",
                        ClassRules.RULE_NO_MODELS_OR_SERVICES_OUTSIDE_OF_SERVICE_PACKAGE_CONVENTION_MATCHED),
                Arguments.of("RULE_NO_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED",
                        ClassRules.RULE_NO_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED),
                Arguments.of("RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED",
                        RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED),
                //--- domain rules
                Arguments.of("RULE_DATAMODEL_IN_DOMAIN_HAS_NO_ENDING_CONVENTION_MATCHED",
                        ClassRules.RULE_DATAMODEL_IN_DOMAIN_HAS_NO_ENDING_CONVENTION_MATCHED),
                Arguments.of("RULE_NO_ENTITIES_OR_REPOS_OUTSIDE_OF_DOMAIN_PACKAGE_CONVENTION_MATCHED",
                        ClassRules.RULE_NO_ENTITIES_OR_REPOS_OUTSIDE_OF_DOMAIN_PACKAGE_CONVENTION_MATCHED),
                Arguments.of("RULE_NO_CROSS_DEPENDENCIES_INSIDE_DOMAIN_CONVENTION_MATCHED",
                        ClassRules.RULE_NO_CROSS_DEPENDENCIES_INSIDE_DOMAIN_CONVENTION_MATCHED));
    }
}

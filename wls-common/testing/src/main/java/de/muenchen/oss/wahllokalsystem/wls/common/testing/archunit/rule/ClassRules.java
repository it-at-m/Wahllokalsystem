package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import java.util.Arrays;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassRules {

    public static final ArchRule RULE_DATAMODEL_IN_REST_ENDS_WITH_DTO_CONVENTION_MATCHED = classes()
            .that().resideInAnyPackage("..rest..").and().areRecords().should().haveSimpleNameEndingWith("DTO");

    public static final ArchRule RULE_DATAMODEL_IN_SERVICE_ENDS_WITH_MODEL_CONVENTION_MATCHED = classes()
            .that().resideInAnyPackage("..service..").and().areRecords().should().haveSimpleNameEndingWith("Model");

    public static final ArchRule RULE_DATAMODEL_IN_DOMAIN_HAS_NO_ENDING_CONVENTION_MATCHED = classes()
            .that().areAnnotatedWith(Entity.class).or().areAnnotatedWith(Embeddable.class)
            .should().haveSimpleNameNotEndingWith("DTO")
            .andShould().haveSimpleNameNotEndingWith("Model")
            .andShould().haveSimpleNameNotEndingWith("Entity");

    // no @entity or @embeddable or repository outside of domain
    public static final ArchRule RULE_NO_ENTITIES_OR_REPOS_OUTSIDE_OF_DOMAIN_PACKAGE_CONVENTION_MATCHED = noClasses()
            .that().areAnnotatedWith(Entity.class)
            .or().areAnnotatedWith(Embeddable.class)
            .or().haveSimpleNameEndingWith("Repository")
            .should().resideOutsideOfPackage("..domain..");

    // no model or service outside of service
    public static final ArchRule RULE_NO_MODELS_OR_SERVICES_OUTSIDE_OF_SERVICE_PACKAGE_CONVENTION_MATCHED = noClasses()
            .that().haveSimpleNameEndingWith("Model")
            .or().haveSimpleNameEndingWith("Service")
            .should().resideOutsideOfPackage("..service..");

    // no dto or controller  outside of rest
    public static final ArchRule RULE_NO_DTOS_OR_CONTROLLERS_OUTSIDE_OF_REST_PACKAGE_CONVENTION_MATCHED = noClasses()
            .that().haveSimpleNameEndingWith("DTO")
            .or().haveSimpleNameEndingWith("Controller")
            .should().resideOutsideOfPackage("..rest..");

    // dto files do not import from service or domain packages
    public static final ArchRule RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_REST_CONVENTION_MATCHED = noClasses()
            .that().resideInAnyPackage("..rest..").and().haveSimpleNameEndingWith("DTO")
            .should().dependOnClassesThat().resideInAnyPackage("..service..", "..domain..");

    // model files do not import from rest or domain packages
    public static final ArchRule RULE_NO_DATAMODEL_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED = noClasses()
            .that().resideInAnyPackage("..service..").and().haveSimpleNameEndingWith("Model")
            .should().dependOnClassesThat().resideInAnyPackage("..rest..", "..domain..");

    // files in rest package do not import from domain package
    public static final ArchRule RULE_NO_CROSS_DEPENDENCIES_INSIDE_REST_CONVENTION_MATCHED = noClasses()
            .that().resideInAnyPackage("..rest..")
            .should().dependOnClassesThat().resideInAnyPackage("..domain..");

    // files in service package do not import from rest packages
    public static final ArchRule RULE_NO_CROSS_DEPENDENCIES_INSIDE_SERVICE_CONVENTION_MATCHED = noClasses()
            .that().resideInAnyPackage("..service..")
            .should().dependOnClassesThat().resideInAnyPackage("..rest..");

    // files in domain package do not import from rest or service packages
    public static final ArchRule RULE_NO_CROSS_DEPENDENCIES_INSIDE_DOMAIN_CONVENTION_MATCHED = noClasses()
            .that().resideInAnyPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("..rest..", "..service..");

    public static final ArchRule RULE_FILES_IN_TEST_END_WITH_TEST_CONVENTION_MATCHED = classes()
            .that().resideInAnyPackage("..test..")
            .and().resideOutsideOfPackage("..test.utils..")
            .and().haveSimpleNameNotEndingWith("TestConstants")
            .and().areNotAnnotatedWith(Nested.class)
            .should().haveSimpleNameEndingWith("Test");

    private static final ArchCondition<JavaClass> haveMatchingPublicMethodName =
            new ArchCondition<>("have matching public method name") {
                @Override
                public void check(JavaClass classWithNestedAnnotation, ConditionEvents events) {
                    val expectedMethodName = StringUtils.uncapitalize(classWithNestedAnnotation.getSimpleName());
                    var topEnclosingClass = getTopEnclosingClass(classWithNestedAnnotation);

                    if (topEnclosingClass.isPresent()) {
                        try {
                            val nestedTestClassFullname = topEnclosingClass.get().getFullName();

                            val nestedClassNameWithoutTestSuffix = nestedTestClassFullname.substring(0, nestedTestClassFullname.lastIndexOf("Test"));
                            val testedClass = Class.forName(nestedClassNameWithoutTestSuffix);   // sollte de.muenchen...NotARecordSoEndingIsNotRelevant suchen

                            val testedClassHasPublicMehodMatchingNestedClassName = Arrays.stream(testedClass.getMethods())
                                    .anyMatch(method -> method.getName().equals(expectedMethodName));
                            System.out.println("\n\n has matching " + testedClassHasPublicMehodMatchingNestedClassName);

                            if (!testedClassHasPublicMehodMatchingNestedClassName) {
                                events.add(SimpleConditionEvent.violated(classWithNestedAnnotation,
                                        "tested class \"" + nestedTestClassFullname + "\" has no public method matching the nested class name: " + expectedMethodName));
                            }
                        } catch (Exception e) {
                            events.add(SimpleConditionEvent.violated(classWithNestedAnnotation, "expected class related to test suite not found: " + topEnclosingClass.get().getFullName()));
                        }
                    } else {
                        events.add(SimpleConditionEvent.violated(classWithNestedAnnotation, "nested annotation is not on an inner class"));
                    }
                }
            };

    private static Optional<JavaClass> getTopEnclosingClass(final JavaClass item) {
        var enclosingClass = item.getEnclosingClass();
        while (enclosingClass.isPresent() && enclosingClass.get().getEnclosingClass().isPresent()) {
            enclosingClass = enclosingClass.get().getEnclosingClass();
        }
        return enclosingClass;
    }

    public static final ArchRule RULE_NESTED_TESTSUITE_HAS_CORRESPONDING_PUBLIC_METHOD_CONVENTION_MATCHED = classes()
            .that().areAnnotatedWith(Nested.class)
            .and().haveSimpleNameNotEndingWith("SecurityConfigurationTest")
            .and().haveSimpleNameNotEndingWith("SecurityTest")
            .should(haveMatchingPublicMethodName);
}

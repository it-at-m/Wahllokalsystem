package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
}

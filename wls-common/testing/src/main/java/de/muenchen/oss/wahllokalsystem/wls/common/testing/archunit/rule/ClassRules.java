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

    public static final ArchRule RULE_DATAMODEL_IN_SERVICE_ENDS_WITH_Model_CONVENTION_MATCHED = classes()
            .that().resideInAnyPackage("..service..").and().areRecords().should().haveSimpleNameEndingWith("Model");

    public static final ArchRule RULE_DATAMODEL_IN_DOMAIN_HAS_NO_ENDING_CONVENTION_MATCHED = classes()
            .that().resideInAnyPackage("..domain..").and().areNotEnums().and().areNotInterfaces()
            .should().beAnnotatedWith(Entity.class).orShould().beAnnotatedWith(Embeddable.class)
            .andShould().haveSimpleNameNotEndingWith("DTO")
            .andShould().haveSimpleNameNotEndingWith("Model")
            .andShould().haveSimpleNameNotEndingWith("Entity");

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
}

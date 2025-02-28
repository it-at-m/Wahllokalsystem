package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.lang.ArchRule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassRules {

    public static final ArchRule RULE_DATAMODEL_IN_REST_ENDS_WITH_DTO_CONVENTION_MATCHED = classes()
            .that().resideInAPackage("..rest..").and().areRecords().should().haveSimpleNameEndingWith("DTO");

    public static final ArchRule RULE_DATAMODEL_IN_SERVICE_ENDS_WITH_Model_CONVENTION_MATCHED = classes()
            .that().resideInAPackage("..service..").and().areRecords().should().haveSimpleNameEndingWith("Model");

    public static final ArchRule RULE_DATAMODEL_IN_DOMAIN_HAS_NO_ENDING_CONVENTION_MATCHED = classes()
            .that().resideInAPackage("..domain..")
            .should().haveSimpleNameNotEndingWith("DTO")
            .andShould().haveSimpleNameNotEndingWith("Model")
            .andShould().haveSimpleNameNotEndingWith("Entity");
}

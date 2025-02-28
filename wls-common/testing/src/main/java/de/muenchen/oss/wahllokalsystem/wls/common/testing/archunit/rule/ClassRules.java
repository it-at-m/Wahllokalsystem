package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.lang.ArchRule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassRules {

    public static final ArchRule RULE_DATAMODEL_IN_REST_ENDS_WITH_DTO_CONVENTION_MATCHED = classes()
            .that().resideInAPackage("..rest..").and().areRecords().should().haveSimpleNameEndingWith("DTO");
}

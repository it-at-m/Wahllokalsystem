package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.elements.MethodsShouldConjunction;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.condition.RequestMappingMethodParameterAnnotationCondition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.web.bind.annotation.RequestMapping;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodRules {

    public static final ArchRule REQUEST_MAPPING_METHODS_SHOULD_USE_ONLY_ANNOTATED_PARAMETERS = methods()
            .that().areAnnotatedWith(RequestMapping.class)
            .or().areMetaAnnotatedWith(RequestMapping.class)
            .should(new RequestMappingMethodParameterAnnotationCondition());

    public static final MethodsShouldConjunction RULE_TEST_NAMING_CONVENTION_SHOULD_WHEN_MATCHED = methods()
            .that().areAnnotatedWith(Test.class).or().areAnnotatedWith(ParameterizedTest.class)
            .should().haveNameMatching("^should_[a-z].*_when_[a-z].*");
}

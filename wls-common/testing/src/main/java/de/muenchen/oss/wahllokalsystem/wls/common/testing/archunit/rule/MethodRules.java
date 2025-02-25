package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.rule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.tngtech.archunit.lang.ArchRule;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.condition.RequestMappingMethodParameterAnnotationCondition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodRules {

    public static final ArchRule REQUEST_MAPPING_METHODS_SHOULD_USE_ONLY_ANNOTATED_PARAMETERS = methods()
            .that().areAnnotatedWith(RequestMapping.class)
            .or().areMetaAnnotatedWith(RequestMapping.class)
            .should(new RequestMappingMethodParameterAnnotationCondition());
}

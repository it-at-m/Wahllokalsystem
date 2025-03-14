package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.condition;

import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.Set;
import lombok.val;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Checks that the at least one of the {@link #requiredAnnotations} is given on each parameter
 */
public class RequestMappingMethodParameterAnnotationCondition extends ArchCondition<JavaMethod> {

    final Set<Object> requiredAnnotations = Set.of(PathVariable.class, RequestBody.class, RequestParam.class, RequestHeader.class);

    public RequestMappingMethodParameterAnnotationCondition() {
        super("Checks that the at least one of the requiredAnnotations is given on each parameter");
    }

    @Override
    public void check(JavaMethod item, ConditionEvents events) {
        val methodParameters = item.getParameters();
        methodParameters.forEach(parameter -> {
            val rawAnnotationsOfParameter = parameter.getAnnotations().stream().map(archUnitAnnotation -> archUnitAnnotation.getRawType().reflect())
                    .toList();
            val parameterHasOneOfRequireAnnotations = requiredAnnotations.stream().anyMatch(rawAnnotationsOfParameter::contains);

            val message = String.format("parameter #%d of method %s in class %s has not one the required annotations %s", parameter.getIndex(),
                    item.getName(),
                    item.reflect().getDeclaringClass().getName(), requiredAnnotations);

            if (!parameterHasOneOfRequireAnnotations) {
                events.add(SimpleConditionEvent.violated(item, message));
            }
        });
    }
}

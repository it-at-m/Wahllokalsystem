package de.muenchen.oss.wahllokalsystem.basisdatenservice.archunit;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;

@Slf4j
public class MethodWithSecurityPredicate extends DescribedPredicate<JavaMethod> {

    public MethodWithSecurityPredicate() {
        super("method with security annotation");
    }

    @Override
    public boolean test(JavaMethod javaMethod) {
        log.debug("testing item > {}", javaMethod.getName());
        return javaMethod.getAnnotations().stream()
                .anyMatch(annotation -> annotation.getType().getName().equals(PreAuthorize.class.getName()));
    }
}

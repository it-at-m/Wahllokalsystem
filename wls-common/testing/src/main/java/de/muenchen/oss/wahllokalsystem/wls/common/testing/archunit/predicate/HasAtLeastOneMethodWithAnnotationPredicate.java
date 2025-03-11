package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.predicate;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import java.lang.annotation.Annotation;
import org.junit.jupiter.api.Test;

public class HasAtLeastOneMethodWithAnnotationPredicate extends DescribedPredicate<JavaClass> {

    private final Class<? extends Annotation> annotationClass;

    public HasAtLeastOneMethodWithAnnotationPredicate(final Class<? extends Annotation> annotationClass) {
        super("have at least one method annotated with @" + annotationClass.getSimpleName());
        this.annotationClass = annotationClass;
    }

    public static final DescribedPredicate<JavaClass> haveAtLeastOneMethodAnnotatedWithTest = new HasAtLeastOneMethodWithAnnotationPredicate(Test.class);

    @Override
    public boolean test(final JavaClass javaClass) {
        return javaClass.getMethods()
                .stream()
                .anyMatch(method -> method.isAnnotatedWith(annotationClass));
    }
}

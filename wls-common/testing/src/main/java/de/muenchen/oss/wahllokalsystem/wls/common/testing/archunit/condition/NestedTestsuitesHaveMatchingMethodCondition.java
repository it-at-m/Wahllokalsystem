package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.condition;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.springframework.util.StringUtils;

public class NestedTestsuitesHaveMatchingMethodCondition extends ArchCondition<JavaClass> {

    public static final ArchCondition<JavaClass> haveMatchingPublicMethodNameIfTheyAreHighestNestedClass = new NestedTestsuitesHaveMatchingMethodCondition();

    public NestedTestsuitesHaveMatchingMethodCondition() {
        super("have matching public method name if they are highest nested class");
    }

    @Override
    public void check(JavaClass classWithNestedAnnotation, ConditionEvents events) {
        var topNestedClass = getTopNestedClass(classWithNestedAnnotation);
        var topEnclosingClass = getTopEnclosingClass(classWithNestedAnnotation);

        if (topEnclosingClass.isPresent()) {

            // exclude specific testfiles from @nested naming convention
            String classNameString = topEnclosingClass.map(JavaClass::getFullName).orElse("failed to extract class name");
            if (exclusions.stream().anyMatch(classNameString::endsWith)) {
                return;
            }

            try {
                val nestedTestClassFullname = topEnclosingClass.get().getFullName();

                val nestedClassNameWithoutTestSuffix = nestedTestClassFullname.substring(0, nestedTestClassFullname.lastIndexOf("Test"));
                val testedClass = Class.forName(nestedClassNameWithoutTestSuffix);

                val expectedMethodName = StringUtils.uncapitalize(topNestedClass.getSimpleName());
                val testedClassHasPublicMehodMatchingNestedClassName = Arrays.stream(testedClass.getMethods())
                        .anyMatch(method -> method.getName().equals(expectedMethodName));

                if (!testedClassHasPublicMehodMatchingNestedClassName) {
                    events.add(SimpleConditionEvent.violated(classWithNestedAnnotation,
                            "tested class \"" + nestedTestClassFullname + "\" has no public method matching the nested class name: " + expectedMethodName));
                }
            } catch (ClassNotFoundException e) {
                events.add(SimpleConditionEvent.violated(classWithNestedAnnotation,
                        "expected class related to test suite not found: " + topEnclosingClass.get().getFullName()));
            }
        } else {
            events.add(SimpleConditionEvent.violated(classWithNestedAnnotation, "nested annotation is not on an inner class"));
        }
    }

    private Optional<JavaClass> getTopEnclosingClass(final JavaClass item) {
        var enclosingClass = item.getEnclosingClass();
        while (enclosingClass.isPresent() && enclosingClass.get().getEnclosingClass().isPresent()) {
            enclosingClass = enclosingClass.get().getEnclosingClass();
        }
        return enclosingClass;
    }

    private JavaClass getTopNestedClass(final JavaClass item) {
        var currentClass = item;
        JavaClass highestNested = null;

        while (currentClass.getEnclosingClass().isPresent()) {
            currentClass = currentClass.getEnclosingClass().orElseThrow();

            if (currentClass.isAnnotatedWith(Nested.class)) {
                highestNested = currentClass;
            } else {
                break;
            }
        }

        // when item already is highest nested class
        highestNested = highestNested == null ? item : highestNested;

        return highestNested;
    }

    private final Set<String> exclusions = Set.of(
            "failed to extract class name",
            "ControllerIntegrationTest",
            "ServiceSecurityTest",
            "SecurityConfigurationTest",
            "ArchUnitTest",
            "SwaggerConfigurationTest");
}

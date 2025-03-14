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

    private final Set<String> classNameEndingExclusions;

    public NestedTestsuitesHaveMatchingMethodCondition() {
        super("have matching public method name if they are highest nested class");
        this.classNameEndingExclusions = Set.of(
                "IntegrationTest",
                "ConfigurationTest",
                "ServiceSecurityTest",
                "ArchUnitTest");
    }

    public NestedTestsuitesHaveMatchingMethodCondition(final Set<String> customExclusions) {
        super("have matching public method name if they are highest nested class");
        this.classNameEndingExclusions = Set.copyOf(customExclusions);
    }

    public static final ArchCondition<JavaClass> haveMatchingPublicMethodNameIfTheyAreHighestNestedClass = new NestedTestsuitesHaveMatchingMethodCondition();

    @Override
    public void check(JavaClass classWithNestedAnnotation, ConditionEvents events) {
        var topNestedClass = getTopNestedClass(classWithNestedAnnotation);
        var topEnclosingClass = getTopEnclosingClass(classWithNestedAnnotation);

        // only check rule for topNestedClasses
        if (topNestedClass.equals(classWithNestedAnnotation)) {
            if (topEnclosingClass.isPresent()) {
                try {
                    val testSuiteClassFullName = topEnclosingClass.get().getFullName();
                    // exclude specific testfiles from @nested naming convention
                    if (classNameEndingExclusions.stream().anyMatch(testSuiteClassFullName::endsWith)) {
                        return;
                    }

                    val testedClassFullName = testSuiteClassFullName.substring(0, testSuiteClassFullName.lastIndexOf("Test"));
                    val testedClass = Class.forName(testedClassFullName);

                    val expectedMethodName = StringUtils.uncapitalize(topNestedClass.getSimpleName());
                    val testedClassHasPublicMethodMatchingNestedClassName = Arrays.stream(testedClass.getMethods())
                            .anyMatch(method -> method.getName().equals(expectedMethodName));

                    if (!testedClassHasPublicMethodMatchingNestedClassName) {
                        events.add(SimpleConditionEvent.violated(classWithNestedAnnotation,
                                "tested class \"" + testSuiteClassFullName + "\" has no public method matching the nested class name: " + expectedMethodName));
                    }
                } catch (final ClassNotFoundException e) {
                    events.add(SimpleConditionEvent.violated(classWithNestedAnnotation,
                            "expected class related to test suite not found: " + topEnclosingClass.get().getFullName()));
                }
            } else {
                events.add(SimpleConditionEvent.violated(classWithNestedAnnotation, "nested annotation is not on an inner class"));
            }
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
}

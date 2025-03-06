package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.condition;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.Arrays;
import java.util.Optional;
import lombok.val;
import org.springframework.util.StringUtils;

public class NestedTestsuitesHaveMatchingMethodCondition {

    public static final ArchCondition<JavaClass> haveMatchingPublicMethodName = new ArchCondition<>("have matching public method name") {
        @Override
        public void check(JavaClass classWithNestedAnnotation, ConditionEvents events) {
            val expectedMethodName = StringUtils.uncapitalize(classWithNestedAnnotation.getSimpleName());
            var topEnclosingClass = getTopEnclosingClass(classWithNestedAnnotation);

            if (topEnclosingClass.isPresent()) {

                // exclude specific testfiles from @nested naming convention
                String classNameString = topEnclosingClass.map(JavaClass::getFullName).orElse("failed to extract class name");
                if (classNameString.endsWith("failed to extract class name")
                        || classNameString.endsWith("ControllerIntegrationTest")
                        || classNameString.endsWith("ServiceSecurityTest")
                        || classNameString.endsWith("SecurityConfigurationTest")
                        || classNameString.endsWith("ArchUnitTest")
                        || classNameString.endsWith("SwaggerConfigurationTest")) {
                    System.out.println("skipped testfile");
                    return;
                }

                try {
                    val nestedTestClassFullname = topEnclosingClass.get().getFullName();

                    val nestedClassNameWithoutTestSuffix = nestedTestClassFullname.substring(0, nestedTestClassFullname.lastIndexOf("Test"));
                    val testedClass = Class.forName(nestedClassNameWithoutTestSuffix);

                    val testedClassHasPublicMehodMatchingNestedClassName = Arrays.stream(testedClass.getMethods())
                            .anyMatch(method -> method.getName().equals(expectedMethodName));

                    if (!testedClassHasPublicMehodMatchingNestedClassName) {
                        events.add(SimpleConditionEvent.violated(classWithNestedAnnotation,
                                "tested class \"" + nestedTestClassFullname + "\" has no public method matching the nested class name: " + expectedMethodName));
                    }
                } catch (Exception e) {
                    events.add(SimpleConditionEvent.violated(classWithNestedAnnotation,
                            "expected class related to test suite not found: " + topEnclosingClass.get().getFullName()));
                }
            } else {
                events.add(SimpleConditionEvent.violated(classWithNestedAnnotation, "nested annotation is not on an inner class"));
            }
        }
    };

    private static Optional<JavaClass> getTopEnclosingClass(final JavaClass item) {
        var enclosingClass = item.getEnclosingClass();
        while (enclosingClass.isPresent() && enclosingClass.get().getEnclosingClass().isPresent()) {
            enclosingClass = enclosingClass.get().getEnclosingClass();
        }
        return enclosingClass;
    }
}

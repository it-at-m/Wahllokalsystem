package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.archunit.condition;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.Arrays;
import java.util.Optional;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

public class NestedTestClassReferencesMethodsInTestedClass extends ArchCondition<JavaClass> {

    public NestedTestClassReferencesMethodsInTestedClass() {
        super("nested class has to reference method in tested class");
    }

    @Override
    public void check(JavaClass item, ConditionEvents events) {
        val expectedMethodName = StringUtils.uncapitalize(item.getSimpleName());

        var topEnclosingClass = getTopEnclosingClass(item);

        if (topEnclosingClass.isPresent()) {
            try {
                val nestedTestClassFullname = topEnclosingClass.get().getFullName();
                val testedClass = Class.forName(nestedTestClassFullname.substring(0, nestedTestClassFullname.lastIndexOf("Test")));
                val testedClassHasPublicMehodMathingNestedClassName = Arrays.stream(testedClass.getMethods())
                        .anyMatch(method -> method.getName().equals(expectedMethodName));

                if (!testedClassHasPublicMehodMathingNestedClassName) {
                    events.add(SimpleConditionEvent.violated(item,
                            "tested class \"" + nestedTestClassFullname + "\" has no public method matching the nested class name: " + expectedMethodName));
                }
            } catch (final ClassNotFoundException e) {
                events.add(SimpleConditionEvent.violated(item, "expected class related to test suit not found: " + topEnclosingClass.get().getFullName()));
            }
        } else {
            events.add(SimpleConditionEvent.violated(item, "nested annotation is not on an inner class"));
        }
    }

    private Optional<JavaClass> getTopEnclosingClass(final JavaClass item) {
        var enclosingClass = item.getEnclosingClass();
        while (enclosingClass.isPresent() && enclosingClass.get().getEnclosingClass().isPresent()) {
            enclosingClass = enclosingClass.get().getEnclosingClass();
        }

        return enclosingClass;
    }
}

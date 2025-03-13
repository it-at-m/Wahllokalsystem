package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.condition;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.Optional;

public class TestClassesEndWithTestCondition extends ArchCondition<JavaMethod> {

    TestClassesEndWithTestCondition() {
        super("have top enclosing class name ending with `Test`");
    }

    public static final ArchCondition<JavaMethod> haveTopEnclosingClassEndingWithTest = new TestClassesEndWithTestCondition();

    @Override
    public void check(JavaMethod method, ConditionEvents events) {
        var topEnclosingClass = getTopEnclosingClass(method.getOwner());

        System.out.println("method: " + method + " + topEnclosingClass: " + topEnclosingClass);

        if (topEnclosingClass.isPresent() && topEnclosingClass.get().getFullName().endsWith("Test")) {
            return;
        } else {
            events.add(SimpleConditionEvent.violated(method, "test " + method + " is not inside of test class"));
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

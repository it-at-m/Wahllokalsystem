package de.muenchen.oss.wahllokalsystem.basisdatenservice.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaCodeUnit;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class DetectAuthorityChain extends ArchCondition<JavaMethod> {

    private Map<JavaMethod, List<JavaCodeUnit>> methodCalls = new HashMap<>();

    public DetectAuthorityChain() {
        super("detecting authority chain");
    }

    @Override
    public void check(JavaMethod item, ConditionEvents events) {
        log.info("-------------------------------");
        log.info("checking item > {}", item.getFullName());

        val checkedMethods = new HashSet<String>();
        val callsFromRestController = getOwningRestControllerMethods(item, checkedMethods);

        callsFromRestController.forEach(methodCall -> {
            log.info("method call {} owned by {}", methodCall.getName(), methodCall.getOwner().getFullName());
        });

        if (!callsFromRestController.isEmpty()) {
            methodCalls.put(item, callsFromRestController);
        }
    }

    private List<JavaCodeUnit> getOwningRestControllerMethods(final JavaMethod methodToCheck, final Set<String> checkedMethods) {
        val callingMethodsFromRestController = new LinkedList<JavaCodeUnit>();

        methodToCheck.getCallsOfSelf().forEach(callingMethod -> {
            if (!checkedMethods.contains(callingMethod.getOwner().getFullName() + callingMethod.getName())) {
                if (isCallerPartOfRestController(callingMethod)) {
                    callingMethodsFromRestController.add(callingMethod.getOwner());
                } else {
                    if (callingMethod.getOwner() instanceof JavaMethod cm) {
                        checkedMethods.add(callingMethod.getOwner().getFullName() + callingMethod.getName());
                        callingMethodsFromRestController.addAll(getOwningRestControllerMethods(cm, checkedMethods));
                    }
                }
            }
        });

        return callingMethodsFromRestController;
    }

    private boolean isCallerPartOfRestController(final JavaMethodCall callingMethod) {
        return isRestController(callingMethod.getOwner().getOwner());
    }

    private boolean isRestController(JavaClass owner) {
        return owner.isAnnotatedWith("org.springframework.web.bind.annotation.RestController");
    }

    public Map<JavaMethod, List<JavaCodeUnit>> getMethodCalls() {
        return methodCalls;
    }
}

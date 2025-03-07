package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utils.correctFileNamingAndDependencies.rest;

import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utils.correctFileNamingAndDependencies.service.ExampleModel;
import java.util.List;

public interface ExampleInterface {
    ExampleModel exampleModel = new ExampleModel();

    String methodWithOverload();

    List<String> methodWithOverload(String input);

    Integer anotherMethodWithOverload();

    List<Integer> AnotherMethodWithOverload(Integer input);
}

package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.incorrectFilenamingAndDependencies.rest;

import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.correctFileNamingAndDependencies.service.ExampleModel;

public record ExampleDto() {
    static ExampleModel exampleModel = new ExampleModel();
}

package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.incorrectFilenamingAndDependencies.service;

import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.incorrectFilenamingAndDependencies.rest.ExampleDto;

public record DataModelClass() {
    static ExampleDto exampleDto = new ExampleDto();
}

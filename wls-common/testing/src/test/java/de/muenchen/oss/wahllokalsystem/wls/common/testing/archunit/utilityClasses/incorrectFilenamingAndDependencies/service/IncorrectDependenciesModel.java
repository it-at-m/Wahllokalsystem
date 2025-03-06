package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.incorrectFilenamingAndDependencies.service;

import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.incorrectFilenamingAndDependencies.domain.ExampleDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.incorrectFilenamingAndDependencies.rest.ExampleDto;

public record IncorrectDependenciesModel() {
    static ExampleDto exampleDto = new ExampleDto();
    static ExampleDTO exampleDTO = new ExampleDTO();
}

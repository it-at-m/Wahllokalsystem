package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utils.incorrectFilenamingAndDependencies.service;

import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utils.incorrectFilenamingAndDependencies.domain.ExampleDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utils.incorrectFilenamingAndDependencies.rest.ExampleDto;

public record IncorrectDependenciesModel() {
    static ExampleDto exampleDto = new ExampleDto();
    static ExampleDTO exampleDTO = new ExampleDTO();
}

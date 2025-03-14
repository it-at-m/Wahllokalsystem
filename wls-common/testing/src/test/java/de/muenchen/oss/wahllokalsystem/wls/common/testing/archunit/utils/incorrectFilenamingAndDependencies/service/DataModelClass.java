package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utils.incorrectFilenamingAndDependencies.service;

import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utils.incorrectFilenamingAndDependencies.rest.ExampleDto;

public record DataModelClass() {
    static ExampleDto exampleDto = new ExampleDto();
}

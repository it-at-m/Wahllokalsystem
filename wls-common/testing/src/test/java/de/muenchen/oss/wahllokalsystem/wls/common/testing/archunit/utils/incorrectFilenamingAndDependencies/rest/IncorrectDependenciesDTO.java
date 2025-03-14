package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utils.incorrectFilenamingAndDependencies.rest;

import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utils.incorrectFilenamingAndDependencies.domain.ExampleDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utils.incorrectFilenamingAndDependencies.service.DataModelClass;

public record IncorrectDependenciesDTO() {
    static ExampleDTO exampleDTO = new ExampleDTO();
    static DataModelClass dataModelClass = new DataModelClass();
}

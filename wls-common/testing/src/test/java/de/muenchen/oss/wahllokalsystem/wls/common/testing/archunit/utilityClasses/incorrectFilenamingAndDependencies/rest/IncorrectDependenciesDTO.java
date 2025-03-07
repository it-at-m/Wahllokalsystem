package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.incorrectFilenamingAndDependencies.rest;

import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.incorrectFilenamingAndDependencies.domain.ExampleDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.incorrectFilenamingAndDependencies.service.DataModelClass;

public record IncorrectDependenciesDTO() {
    static ExampleDTO exampleDTO = new ExampleDTO();
    static DataModelClass dataModelClass = new DataModelClass();
}

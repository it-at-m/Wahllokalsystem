package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.incorrectFilenamingAndDependencies.service;

import de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.incorrectFilenamingAndDependencies.domain.ExampleEntity;

public record ExampleMODEL() {
    static ExampleEntity exampleEntity = new ExampleEntity();
}

package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utilityClasses.incorrectFilenamingAndDependencies.service;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public record ExampleMODEL() {
    @Id
    private static Long id;
}

package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.utils.correctFileNamingAndDependencies.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Example {

    @Id
    private Long id;
}

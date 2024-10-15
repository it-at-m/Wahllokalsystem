package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.infomanagementClient;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record KonfigurierterWahltagModel(@NotNull LocalDate wahltag,
                                         @NotNull @Size(max = 255) String wahltagID,
                                         boolean active,
                                         @NotNull @Size(max = 255) String nummer) {
}

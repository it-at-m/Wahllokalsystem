package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record KonfigurierterWahltagModel(@NotNull LocalDate wahltag,
                                         @NotNull @Size(max = 255) String wahltagID,
                                         boolean active,
                                         @NotNull @Size(max = 255) String nummer) {
}

package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record KonfigurierterWahltagModel(@NotNull LocalDate wahltag,
                                         @NotNull @Size(max = 255) String wahltagID,
                                         boolean active,
                                         @NotNull @Size(max = 255) String nummer) {
}

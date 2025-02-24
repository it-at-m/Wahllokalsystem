package de.muenchen.oss.wahllokalsystem.adminservice.rest.konfigurierterwahltag;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record KonfigurierterWahltagDTO(@NotNull LocalDate wahltag, @NotNull @Size(max = 255) String wahltagID,
                                       boolean active, @NotNull @Size(max = 255) String nummer) {
}

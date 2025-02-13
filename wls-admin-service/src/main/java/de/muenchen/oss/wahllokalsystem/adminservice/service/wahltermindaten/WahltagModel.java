package de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record WahltagModel(@NotNull String wahltagID,
                           @NotNull LocalDate wahltag,
                           String beschreibung,
                           String nummer) {
}

package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltage;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record WahltagDTO(@NotNull String wahltagID,
                         @NotNull LocalDate wahltag,
                         @NotNull String beschreibung,
                         @NotNull String nummer) {
}

package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltag;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record WahltagDTO(@NotNull String wahltagID,
                         @NotNull LocalDate wahltag,
                         String beschreibung,
                         String nummer) {
}

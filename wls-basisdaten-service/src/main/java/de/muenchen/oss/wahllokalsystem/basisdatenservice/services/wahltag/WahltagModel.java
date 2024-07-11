package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record WahltagModel(@NotNull String wahltagID,
                           @NotNull LocalDate wahltag,
                           String beschreibung,
                           String nummer
) {

}

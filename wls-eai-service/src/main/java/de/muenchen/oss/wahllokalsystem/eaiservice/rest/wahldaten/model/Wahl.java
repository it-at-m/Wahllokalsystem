package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.model;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.model.Wahlart;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record Wahl(@NotNull String identifikator,
                   @NotNull String name,
                   @NotNull Wahlart wahlart,
                   @NotNull LocalDate wahltag,
                   @NotNull String nummer) {
}

package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.model;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record Wahltag(@NotNull String identifikator,
                      @NotNull LocalDate tag,
                      @NotNull String beschreibung,
                      @NotNull String nummer) {
}

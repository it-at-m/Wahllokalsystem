package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record WahltagDTO(@NotNull String identifikator,
                         @NotNull LocalDate tag,
                         @NotNull String beschreibung,
                         @NotNull String nummer) {
}

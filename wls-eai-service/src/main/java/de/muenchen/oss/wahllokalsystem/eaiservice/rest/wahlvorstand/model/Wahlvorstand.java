package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

public record Wahlvorstand(@NotNull LocalDateTime anwesenheitBeginn,
                           @NotNull String wahlbezirkID,
                           @NotNull @Size(min = 1) Set<Wahlvorstandsmitglied> mitglieder) {
}

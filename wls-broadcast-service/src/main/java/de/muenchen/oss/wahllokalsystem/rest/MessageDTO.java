package de.muenchen.oss.wahllokalsystem.rest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

public record MessageDTO(UUID oid, @NotNull @Size(max = 1024) String wahlbezirkID, @NotNull @Size(max = 1024) String nachricht, @NotNull LocalDateTime empfangsZeit) {
}

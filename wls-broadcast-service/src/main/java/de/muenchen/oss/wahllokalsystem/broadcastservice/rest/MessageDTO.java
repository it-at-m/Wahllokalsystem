package de.muenchen.oss.wahllokalsystem.broadcastservice.rest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

public record MessageDTO(@NotNull UUID oid, @NotNull @Size(max = 1024) String wahlbezirkID, @NotNull @Size(max = 1024) String nachricht, @NotNull LocalDateTime empfangsZeit) {
}

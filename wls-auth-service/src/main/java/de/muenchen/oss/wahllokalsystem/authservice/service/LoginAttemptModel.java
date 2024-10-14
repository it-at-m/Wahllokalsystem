package de.muenchen.oss.wahllokalsystem.authservice.service;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record LoginAttemptModel(
        @NotNull UUID id,
        @NotNull String username,
        int attempts,
        LocalDateTime lastModified
) {
}

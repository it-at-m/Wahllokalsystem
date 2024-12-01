package de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.controller.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BroadcastMessage(
        @NotNull UUID id,
        String message
) {
}

package de.muenchen.oss.wahllokalsystem.broadcastservice.rest;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BroadcastMessageDTO(@NotNull List<String> wahlbezirkIDs, @NotNull String nachricht) {
    
}

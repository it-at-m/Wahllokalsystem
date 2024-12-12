package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.models;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record VermerkModel(
        @NotNull long blattnummer,
        @NotNull Set<StimmzettelModel> stimmzettelnModel
) {
}

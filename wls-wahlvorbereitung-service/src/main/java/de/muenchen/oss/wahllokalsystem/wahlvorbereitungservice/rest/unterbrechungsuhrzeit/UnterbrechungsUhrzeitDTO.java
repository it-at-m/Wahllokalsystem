package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.unterbrechungsuhrzeit;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record UnterbrechungsUhrzeitDTO(@NotNull String wahlbezirkID,
                                       LocalDateTime unterbrechungsUhrzeit) {

    public UnterbrechungsUhrzeitDTO(final String wahlbezirkID, final LocalDateTime unterbrechungsUhrzeit) {
        this.wahlbezirkID = wahlbezirkID;
        this.unterbrechungsUhrzeit = unterbrechungsUhrzeit;
    }
}

package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.unterbrechungsuhrzeit;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record UnterbrechungsUhrzeitDTO(@NotNull String wahlbezirkID,
                                       @NotNull LocalDateTime unterbrechungsUhrzeit) {

}

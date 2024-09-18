package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlschliessungsuhrzeit;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record UrnenwahlSchliessungsUhrzeitDTO(@NotNull String wahlbezirkID,
                                              @NotNull LocalDateTime urnenwahlSchliessungsUhrzeit) {

}

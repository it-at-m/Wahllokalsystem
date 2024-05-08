package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record KonfigurierterWahltagDTO(@NotNull LocalDate wahltag, @NotNull String wahltagID,
                                       @NotNull WahltagStatus wahltagStatus, @NotNull String nummer) {
}

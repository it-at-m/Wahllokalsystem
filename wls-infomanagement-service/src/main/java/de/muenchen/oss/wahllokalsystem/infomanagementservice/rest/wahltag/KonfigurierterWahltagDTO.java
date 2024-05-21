package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record KonfigurierterWahltagDTO(@NotNull LocalDate wahltag, @NotNull @Size(max = 255) String wahltagID,
                                       @NotNull WahltagStatus wahltagStatus, @NotNull @Size(max = 255) String nummer) {
}

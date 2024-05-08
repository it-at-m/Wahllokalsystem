package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag.WahltagStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record KonfigurierterWahltagModel(@NotNull LocalDate wahltag, @NotNull String wahltagID,
                                         @NotNull WahltagStatus wahltagStatus, @NotNull String nummer) {
}

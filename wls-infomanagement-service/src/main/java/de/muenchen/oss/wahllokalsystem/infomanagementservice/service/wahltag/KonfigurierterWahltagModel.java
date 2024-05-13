package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag.WahltagStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record KonfigurierterWahltagModel(@NotNull LocalDate wahltag, @NotNull @Size(max = 255) String wahltagID,
                                         @NotNull WahltagStatus wahltagStatus, @NotNull @Size(max = 255) String nummer) {
}

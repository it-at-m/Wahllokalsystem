package de.muenchen.oss.wahllokalsystem.adminservice.rest.konfiguriertewahltage;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record KonfigurierterWahltagDTO(@NotNull LocalDate wahltag, @NotNull @Size(max = 255) String wahltagID,
                                       WahltagStatusDTO wahltagStatusDTO, @NotNull @Size(max = 255) String nummer) {
}

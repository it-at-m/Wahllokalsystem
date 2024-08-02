package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlartModel;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record WahlModel(@NotNull String identifikator,
                        @NotNull String name,
                        @NotNull WahlartModel wahlart,
                        @NotNull LocalDate wahltag,
                        @NotNull String nummer) {
}

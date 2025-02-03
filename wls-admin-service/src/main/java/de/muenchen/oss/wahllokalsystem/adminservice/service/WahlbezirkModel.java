package de.muenchen.oss.wahllokalsystem.adminservice.service;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record WahlbezirkModel(@NotNull String wahlbezirkID,
                              @NotNull WahlbezirkArtModel wahlbezirkart,
                              @NotNull String nummer,
                              @NotNull LocalDate wahltag,
                              @NotNull String wahlnummer,
                              @NotNull String wahlID) {
}

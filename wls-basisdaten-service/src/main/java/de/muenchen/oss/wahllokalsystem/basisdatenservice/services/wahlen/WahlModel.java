package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahlart;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record WahlModel(@NotNull String wahlID,
                        @NotNull String name,
                        @NotNull String reihenfolge,
                        Long waehlerverzeichnisnummer,
                        @NotNull java.time.LocalDate wahltag,
                        @NotNull Wahlart wahlart,
                        Farbe farbe,
                        String nummer
) {

}

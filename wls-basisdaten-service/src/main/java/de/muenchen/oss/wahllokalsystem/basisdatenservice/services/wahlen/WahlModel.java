package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Wahlart;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record WahlModel(@NotNull String wahlID,
                        @NotNull String name,
                        @NotNull Long reihenfolge,
                        @NotNull Long waehlerverzeichnisnummer,
                        @NotNull LocalDate wahltag,
                        @NotNull Wahlart wahlart,
                        Farbe farbe,
                        String nummer
) {

}

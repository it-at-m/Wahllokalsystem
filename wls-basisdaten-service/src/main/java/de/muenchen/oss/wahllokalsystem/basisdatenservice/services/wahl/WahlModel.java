package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahl;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlart;
import jakarta.validation.constraints.NotNull;

public record WahlModel(@NotNull String name,
                        @NotNull String reihenfolge,
                        @NotNull long waehlerverzeichnisNummer,
                        @NotNull java.time.LocalDate wahltag,
                        @NotNull Wahlart wahlart,
                        Farbe farbe,
                        String nummer
) {

}

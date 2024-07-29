package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen;


import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahlart;
import jakarta.validation.constraints.NotNull;

public record WahlDTO(@NotNull String name,
                               @NotNull String reihenfolge,
                               @NotNull long waehlerverzeichnisNummer,
                               @NotNull java.time.LocalDate wahltag,
                               @NotNull Wahlart wahlart,
                               Farbe farbe,
                               String nummer
                               ) {
}

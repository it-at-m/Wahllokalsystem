package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen;


import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Wahlart;
import jakarta.validation.constraints.NotNull;

public record WahlDTO(@NotNull String wahlID, @NotNull String name,
       @NotNull Long reihenfolge,
       @NotNull Long waehlerverzeichnisnummer,
       @NotNull java.time.LocalDate wahltag,
       @NotNull Wahlart wahlart,
       Farbe farbe,
       String nummer
       ) {
}

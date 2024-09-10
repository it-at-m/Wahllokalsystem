package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record WahltagWithNummer(
        @NotNull LocalDate wahltag,
        @NotNull String wahltagNummer) {
}

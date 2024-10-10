package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record WahltagWithNummer(
        @NotNull LocalDate wahltag,
        @NotNull String wahltagNummer) {
}

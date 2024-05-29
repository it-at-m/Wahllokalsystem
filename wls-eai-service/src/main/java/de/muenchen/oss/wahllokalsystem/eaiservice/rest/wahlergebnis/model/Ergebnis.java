package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.model;

import jakarta.validation.constraints.NotNull;

public record Ergebnis(@NotNull String stimmenart,
                       long wahlvorschlagsordnungszahl,
                       long ergebnis,
                       String wahlvorschlagID,
                       String kandidatID
) {
}

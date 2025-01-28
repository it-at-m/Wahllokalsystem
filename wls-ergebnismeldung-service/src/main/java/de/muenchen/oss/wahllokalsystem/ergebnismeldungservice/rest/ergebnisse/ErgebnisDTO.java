package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse;

import jakarta.validation.constraints.NotNull;

public record ErgebnisDTO(String wahlvorschlagID, String kandidatID, Long wahlvorschlagsordnungszahl, @NotNull long ergebnis, Long numIndex) {
}

package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.common;

import lombok.Builder;

@Builder
public record WahlurneDTO(String wahlID, long anzahl, Boolean urneVersiegelt) {
}

package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.common;

import lombok.Builder;

@Builder
public record WahlurneModel(String wahlID, long anzahl, Boolean urneVersiegelt) {
}

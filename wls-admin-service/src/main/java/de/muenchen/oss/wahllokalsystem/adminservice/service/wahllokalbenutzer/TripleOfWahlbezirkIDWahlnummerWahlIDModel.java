package de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TripleOfWahlbezirkIDWahlnummerWahlIDModel(
    @NotNull String wahlbezirkID,
    @NotNull String wahlnummer,
    @NotNull String wahlID){
}

package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Builder;

@Builder
public record StimmabgabevermerkeDTO(@NotNull String wahlbezirkID,
                                     @NotNull long waehlerverzeichnisNummer,
                                     @NotNull long anzahlBlaetter,
                                     @NotNull Set<WahldatenDTO> wahldaten){
}

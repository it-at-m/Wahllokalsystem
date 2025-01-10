package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record WahldatenDTO(@NotNull String wahlbezirkID,
                           @NotNull String wahlID,
                           @NotNull long waehlerverzeichnisNummer,
                           @NotNull Set<VermerkDTO> vermerke,
                           @NotNull Set<EingenommenerWahlscheinDTO> eingenommenewahlscheine){
}

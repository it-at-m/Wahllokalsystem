package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record WahldatenModel(
        @NotNull String wahlbezirkID,
        @NotNull String wahlID,
        @NotNull Long waehlerverzeichnisNummer,
        @NotNull Set<VermerkModel> vermerke,
        @NotNull Set<EingenommenerWahlscheinModel> eingenommeneWahlscheine) {
}

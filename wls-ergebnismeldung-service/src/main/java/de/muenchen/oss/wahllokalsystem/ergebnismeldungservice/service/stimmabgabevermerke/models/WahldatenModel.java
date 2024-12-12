package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.models;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.BezirkUndWahlIDUndWaehlerverzeichnisnummer;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record WahldatenModel(
        @NotNull BezirkUndWahlIDUndWaehlerverzeichnisnummer bezirkUndWahlIDUndWaehlerverzeichnisnummer,
        @NotNull Set<VermerkModel> vermerkeModel,
        @NotNull Set<EingenommenerWahlscheinModel> eingenommenewahlscheineModel) {
}

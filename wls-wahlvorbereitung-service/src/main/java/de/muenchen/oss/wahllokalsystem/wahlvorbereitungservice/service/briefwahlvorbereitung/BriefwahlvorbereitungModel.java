package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.briefwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.common.WahlurneModel;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BriefwahlvorbereitungModel(@NotNull String wahlbezirkID, @NotNull java.util.List<WahlurneModel> urnenAnzahl) {
}

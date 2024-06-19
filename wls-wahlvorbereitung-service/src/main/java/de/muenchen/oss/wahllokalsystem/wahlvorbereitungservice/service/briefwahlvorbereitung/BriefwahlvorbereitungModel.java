package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.briefwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Wahlurne;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BriefwahlvorbereitungModel(@NotNull String wahlbezirkID, @NotNull java.util.List<Wahlurne> urnenAnzahl) {

}
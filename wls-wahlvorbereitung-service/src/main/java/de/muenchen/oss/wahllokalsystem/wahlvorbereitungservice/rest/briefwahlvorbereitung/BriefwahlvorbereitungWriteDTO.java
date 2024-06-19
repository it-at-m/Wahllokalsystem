package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.briefwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Wahlurne;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BriefwahlvorbereitungWriteDTO(@NotNull java.util.List<Wahlurne> urnenAnzahl) {

}

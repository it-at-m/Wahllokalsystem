package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WahlUndBezirkIDUndMeldungsartModel(@NotBlank String wahlbezirkID,@NotBlank String wahlID,@NotNull MeldungsartModel meldungsart){}

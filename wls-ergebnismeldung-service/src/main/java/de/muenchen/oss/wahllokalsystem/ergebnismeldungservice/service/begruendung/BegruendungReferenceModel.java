package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder public record BegruendungReferenceModel(@NotNull String wahlbezirkID,@NotNull String wahlID,@NotNull StapelartModel stapelart){}

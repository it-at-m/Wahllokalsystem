package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.aoueai.domain.Wahlvorschlag;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record WahlvorschlaegeModel(@NotNull String wahlID, @NotNull String wahlbezirkID, @NotNull String stimmzettelgebietID, @NotNull java.util.Set<Wahlvorschlag> wahlvorschlaege) {

}

package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.ungueltigewahlscheine;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.common.WahlbezirkArtModel;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder public record UngueltigeWahlscheineReferenceModel(@NotNull String wahltagID,@NotNull WahlbezirkArtModel wahlbezirksart){}

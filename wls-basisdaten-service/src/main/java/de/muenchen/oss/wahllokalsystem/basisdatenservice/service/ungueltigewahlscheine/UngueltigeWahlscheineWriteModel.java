package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.ungueltigewahlscheine;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder public record UngueltigeWahlscheineWriteModel(@NotNull UngueltigeWahlscheineReferenceModel ungueltigeWahlscheineReferenceModel,@NotNull byte[]ungueltigeWahlscheineData){}

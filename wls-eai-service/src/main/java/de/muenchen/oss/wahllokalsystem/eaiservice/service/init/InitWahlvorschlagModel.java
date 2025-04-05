package de.muenchen.oss.wahllokalsystem.eaiservice.service.init;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;

public record InitWahlvorschlagModel(
        long ordnungszahl,
        @NotNull String kurzname,
        Collection<InitKandidatModel> kandidaten
) {
}

package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import java.util.List;

public record WahlenWriteModel(
        String wahltagID,
        List<WahlModel> wahlen
) {
}

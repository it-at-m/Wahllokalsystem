package de.muenchen.oss.wahllokalsystem.eaiservice.service.init;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlbezirkArtDTO;

public record InitWahlbezirkModel(
        int a1,
        int a2,
        int a3,
        WahlbezirkArtDTO wahlbezirkArtDTO
) {
}

package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettel;

import java.util.List;

public record WaehlerStimmzettelDTO(
    String wahlbezirkID,
    String wahlID,
    int stimmzettelNummer,
    List<Integer> selectedWahlvorschlaegeOrdnungszahlen,
    List<StimmzettelKandidatDTO> kandidaten) {}

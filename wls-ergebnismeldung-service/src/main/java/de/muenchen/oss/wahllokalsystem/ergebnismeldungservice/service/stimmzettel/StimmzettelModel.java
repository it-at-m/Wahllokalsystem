package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettel;

import java.util.List;

public record StimmzettelModel(
    String wahlbezirkID,
    String wahlID,
    int stimmzettelNummer,
    List<Integer> selectedWahlvorschlaegeOrdnungszahlen,
    List<StimmzettelKandidatModel> kandidaten) {}

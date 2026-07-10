package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import java.util.List;

public record StimmzettelOfTeamDTO(
    int stimmzettelkennung,
    List<Integer> selectedWahlvorschlaegeOrdnungszahlen,
    List<StimmzettelKandidatDTO> kandidaten) {}

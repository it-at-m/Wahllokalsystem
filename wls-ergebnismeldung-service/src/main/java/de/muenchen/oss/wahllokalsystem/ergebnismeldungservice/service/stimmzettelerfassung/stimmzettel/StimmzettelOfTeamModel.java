package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import java.util.List;

public record StimmzettelOfTeamModel(
    int stimmzettelkennung,
    List<Integer> selectedWahlvorschlaegeOrdnungszahlen,
    List<StimmzettelKandidatModel> kandidaten) {}

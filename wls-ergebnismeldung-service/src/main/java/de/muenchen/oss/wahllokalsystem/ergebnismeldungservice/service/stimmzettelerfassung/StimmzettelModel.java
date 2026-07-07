package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung;

import java.util.List;

public record StimmzettelModel(
    List<Integer> selectedWahlvorschlaegeOrdnungszahlen,
    List<StimmzettelKandidatModel> kandidaten) {}

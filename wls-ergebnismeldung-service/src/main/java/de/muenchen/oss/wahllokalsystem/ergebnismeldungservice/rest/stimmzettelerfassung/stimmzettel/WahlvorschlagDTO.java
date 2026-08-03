package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import java.util.List;

public record WahlvorschlagDTO(
    String wahlvorschlagID, boolean selected, List<KandidatDTO> kandidaten) {}

package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import java.util.List;

public record WahlvorschlagModel(
    String wahlvorschlagID, boolean isSelected, List<KandidatModel> kandidaten) {}

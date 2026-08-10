package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record WahlvorschlagModel(
    @NotNull String wahlvorschlagID, @NotNull Boolean selected, List<KandidatModel> kandidaten) {}

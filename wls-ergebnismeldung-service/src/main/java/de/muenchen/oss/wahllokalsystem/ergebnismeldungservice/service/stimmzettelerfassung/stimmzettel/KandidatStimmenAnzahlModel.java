package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

public record KandidatStimmenAnzahlModel(
    String wahlvorschlagID,
    String kandidatID,
    Long getAnzahl) {
}

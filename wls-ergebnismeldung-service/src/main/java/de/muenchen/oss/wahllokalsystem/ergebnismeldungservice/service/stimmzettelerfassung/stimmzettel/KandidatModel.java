package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

public record KandidatModel(
    KandidatIDModel id,
    boolean isDiscarded,
    Integer votesByVoter,
    Integer invalidVotes,
    Integer votesByWahlvorschlag) {}

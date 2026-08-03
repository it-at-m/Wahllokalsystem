package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

public record KandidatDTO(
    KandidatIDDTO id,
    boolean isDiscarded,
    Integer votesByVoter,
    Integer invalidVotes,
    Integer votesByWahlvorschlag) {}

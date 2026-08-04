package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

public record KandidatDTO(
    KandidatIdDTO id,
    boolean discarded,
    Integer votesByVoter,
    Integer invalidVotes,
    Integer votesByWahlvorschlag) {}

package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

public record KandidatModel(
    KandidatIdModel id,
    boolean discarded,
    Integer votesByVoter,
    Integer invalidVotes,
    Integer votesByWahlvorschlag) {}

package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettel;

public record StimmzettelKandidatDTO(String kandidatId, boolean isDiscarded, int votesByVoter) {}

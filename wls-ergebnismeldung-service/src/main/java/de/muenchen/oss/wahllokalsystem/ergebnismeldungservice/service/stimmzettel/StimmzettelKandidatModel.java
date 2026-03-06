package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettel;

public record StimmzettelKandidatModel(String kandidatId, boolean isDiscarded, int votesByVoter) {}

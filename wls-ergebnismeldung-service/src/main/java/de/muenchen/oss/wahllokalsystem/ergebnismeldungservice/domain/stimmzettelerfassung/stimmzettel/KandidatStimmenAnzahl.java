package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

public interface KandidatStimmenAnzahl {
    String getWahlvorschlagID();

    String getKandidatID();

    Integer getNennungsNummer();

    Long getAnzahl();
}

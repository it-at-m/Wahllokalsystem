package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung;

public record StimmzettelIDModel(
    String wahlbezirkID, String wahlID, String teamID, int stimmzettelkennung) {}

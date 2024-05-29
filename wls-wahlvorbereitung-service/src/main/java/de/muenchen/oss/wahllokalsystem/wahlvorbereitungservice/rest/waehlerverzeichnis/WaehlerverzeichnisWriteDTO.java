package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.waehlerverzeichnis;

public record WaehlerverzeichnisWriteDTO(Boolean verzeichnisLagVor,
                                         Boolean berichtigungVorBeginnDerAbstimmung,
                                         Boolean nachtraeglicheBerichtigung,
                                         Boolean mitteilungUeberUngueltigeWahlscheineErhalten) {
}

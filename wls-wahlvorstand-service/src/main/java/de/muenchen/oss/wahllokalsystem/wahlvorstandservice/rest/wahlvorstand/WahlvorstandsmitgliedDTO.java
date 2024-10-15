package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand;

public record WahlvorstandsmitgliedDTO(String identifikator,
                                       String familienname,
                                       String vorname,
                                       FunktionDTO funktion,
                                       String funktionsname,
                                       boolean anwesend) {
}

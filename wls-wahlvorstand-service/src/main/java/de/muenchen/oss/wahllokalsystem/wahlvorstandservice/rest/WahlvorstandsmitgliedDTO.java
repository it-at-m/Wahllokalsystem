package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest;

public record WahlvorstandsmitgliedDTO(String identifikator,
                                       String familienname,
                                       String vorname,
                                       FunktionDTO funktion,
                                       String funktionsName,
                                       boolean anwesend) {
}

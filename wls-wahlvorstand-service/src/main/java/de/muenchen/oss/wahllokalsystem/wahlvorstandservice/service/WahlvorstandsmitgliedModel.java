package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service;

public record WahlvorstandsmitgliedModel(String identifikator,
                                         String familienname,
                                         String vorname,
                                         FunktionModel funktion,
                                         String funktionsName,
                                         boolean anwesend) {
}
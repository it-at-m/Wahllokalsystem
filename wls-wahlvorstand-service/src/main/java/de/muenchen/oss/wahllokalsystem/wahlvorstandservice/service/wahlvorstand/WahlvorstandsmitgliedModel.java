package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand;

import lombok.Builder;

@Builder
public record WahlvorstandsmitgliedModel (String identifikator,
                                          String familienname,
                                          String vorname,
                                          FunktionModel funktion,
                                          String funktionsname,
                                          boolean anwesend) {
}

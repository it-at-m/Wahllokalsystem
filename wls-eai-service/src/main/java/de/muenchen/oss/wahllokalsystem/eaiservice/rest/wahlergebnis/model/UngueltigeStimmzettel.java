package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.model;

public record UngueltigeStimmzettel(String stimmenart,
                                    Long anzahl,
                                    String wahlvorschlagID) {
}

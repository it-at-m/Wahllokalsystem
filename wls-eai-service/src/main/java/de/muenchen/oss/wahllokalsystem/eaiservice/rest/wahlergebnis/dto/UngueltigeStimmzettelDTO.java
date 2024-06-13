package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto;

public record UngueltigeStimmzettelDTO(String stimmenart,
                                       Long anzahl,
                                       String wahlvorschlagID) {
}

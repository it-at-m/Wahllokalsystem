package de.muenchen.oss.wahllokalsystem.eaiservice.rest.ergebnismeldung.dto;

public record UngueltigeStimmzettelDTO(String stimmenart,
                                       Long anzahl,
                                       String wahlvorschlagID) {
}

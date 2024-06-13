package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record StimmzettelgebietDTO(String identifikator,
                                   String nummer,
                                   String name,
                                   LocalDate wahltag,
                                   @NotNull StimmzettelgebietsartDTO stimmzettelgebietart) {
}

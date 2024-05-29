package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.model;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record Stimmzettelgebiet(String identifikator,
                                String nummer,
                                String name,
                                LocalDate wahltag,
                                @NotNull Stimmzettelgebietsart stimmzettelgebietart) {
}

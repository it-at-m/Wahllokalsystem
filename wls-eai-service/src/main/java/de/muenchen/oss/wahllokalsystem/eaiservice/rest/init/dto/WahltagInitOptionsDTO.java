package de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record WahltagInitOptionsDTO(@NotNull String nummer,
                                    @NotNull LocalDate wahltag,
                                    String beschreibung,
                                    List<WahlInitOptionsDTO> wahlInitOptions) {
}

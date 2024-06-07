package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.dto.WahlartDTO;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record WahlDTO(@NotNull String identifikator,
                      @NotNull String name,
                      @NotNull WahlartDTO wahlart,
                      @NotNull LocalDate wahltag,
                      @NotNull String nummer) {
}

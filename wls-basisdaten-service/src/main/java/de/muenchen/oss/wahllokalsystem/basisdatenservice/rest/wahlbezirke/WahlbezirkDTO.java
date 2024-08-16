package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlbezirke;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record WahlbezirkDTO(@NotNull String wahlbezirkID,
                            @NotNull LocalDate wahltag,
                            @NotNull String nummer,
                            @NotNull WahlbezirkartDTO wahlbezirkart,
                            @NotNull String wahlnummer,
                            @NotNull String wahlID) {
}

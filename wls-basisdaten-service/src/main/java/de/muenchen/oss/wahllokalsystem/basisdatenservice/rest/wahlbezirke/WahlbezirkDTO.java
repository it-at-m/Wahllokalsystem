package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common.WahlbezirkArtDTO;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record WahlbezirkDTO(@NotNull String wahlbezirkID,
                            @NotNull LocalDate wahltag,
                            @NotNull String nummer,
                            @NotNull WahlbezirkArtDTO wahlbezirkart,
                            @NotNull String wahlnummer,
                            @NotNull String wahlID) {
}

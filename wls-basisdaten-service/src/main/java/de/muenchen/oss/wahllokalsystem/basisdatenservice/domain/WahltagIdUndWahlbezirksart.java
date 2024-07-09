package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WahltagIdUndWahlbezirksart {

    @NotNull
    @Size(max = 1024)
    private String wahltagID;

    @Enumerated(EnumType.STRING)
    @NotNull
    private WahlbezirkArt wahlbezirksart;
}

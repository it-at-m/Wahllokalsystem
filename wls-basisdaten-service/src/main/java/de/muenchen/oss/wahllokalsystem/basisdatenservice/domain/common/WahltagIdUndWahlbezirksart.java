package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WahltagIdUndWahlbezirksart {

    @NotNull
    @Size(max = 1024)
    private String wahltagID;

    @Enumerated(EnumType.STRING)
    @NotNull
    private WahlbezirkArt wahlbezirksart;
}

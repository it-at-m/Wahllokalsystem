package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto;

import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record BasisdatenDTO(@NotNull Set<BasisstrukturdatenDTO> basisstrukturdaten,
                            @NotNull Set<WahlDTO> wahlen,
                            @NotNull Set<WahlbezirkDTO> wahlbezirke,
                            @NotNull Set<StimmzettelgebietDTO> stimmzettelgebiete) {

    public BasisdatenDTO(Set<BasisstrukturdatenDTO> basisstrukturdaten,
            Set<WahlDTO> wahlen,
            Set<WahlbezirkDTO> wahlbezirke,
            Set<StimmzettelgebietDTO> stimmzettelgebiete) {
        this.basisstrukturdaten = Objects.requireNonNullElse(basisstrukturdaten, new HashSet<>());
        this.wahlen = Objects.requireNonNullElse(wahlen, new HashSet<>());
        this.wahlbezirke = Objects.requireNonNullElse(wahlbezirke, new HashSet<>());
        this.stimmzettelgebiete = Objects.requireNonNullElse(stimmzettelgebiete, new HashSet<>());
    }
}

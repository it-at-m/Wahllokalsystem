package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.model;

import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record Basisdaten(@NotNull Set<Basisstrukturdaten> basisstrukturdaten,
                         @NotNull Set<Wahl> wahlen,
                         @NotNull Set<Wahlbezirk> wahlbezirke,
                         @NotNull Set<Stimmzettelgebiet> stimmzettelgebiete) {

    public Basisdaten(Set<Basisstrukturdaten> basisstrukturdaten,
            Set<Wahl> wahlen,
            Set<Wahlbezirk> wahlbezirke,
            Set<Stimmzettelgebiet> stimmzettelgebiete) {
        this.basisstrukturdaten = Objects.requireNonNullElse(basisstrukturdaten, new HashSet<>());
        this.wahlen = Objects.requireNonNullElse(wahlen, new HashSet<>());
        this.wahlbezirke = Objects.requireNonNullElse(wahlbezirke, new HashSet<>());
        this.stimmzettelgebiete = Objects.requireNonNullElse(stimmzettelgebiete, new HashSet<>());
    }
}

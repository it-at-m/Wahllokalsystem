package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Builder;

@Builder
public record BasisdatenModel(@NotNull Set<BasisstrukturdatenModel> basisstrukturdaten,
                              @NotNull Set<WahlModel> wahlen,
                              @NotNull Set<WahlbezirkModel> wahlbezirke,
                              @NotNull Set<StimmzettelgebietModel> stimmzettelgebiete) {

}

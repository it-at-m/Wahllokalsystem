package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlbezirke.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlen.WahlModel;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Builder;

@Builder public record BasisdatenModel(@NotNull Set<BasisstrukturdatenModel>basisstrukturdaten,@NotNull Set<WahlModel>wahlen,@NotNull Set<WahlbezirkModel>wahlbezirke,@NotNull Set<StimmzettelgebietModel>stimmzettelgebiete){

}

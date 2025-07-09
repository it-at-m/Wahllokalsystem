package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.kopfdaten;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder public record BasisstrukturdatenModel(@NotNull String wahlID,@NotNull String stimmzettelgebietID,@NotNull String wahlbezirkID,@NotNull LocalDate wahltag){}

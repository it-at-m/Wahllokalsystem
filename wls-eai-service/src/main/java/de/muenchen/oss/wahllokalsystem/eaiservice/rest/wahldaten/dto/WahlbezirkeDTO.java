package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record WahlbezirkeDTO(@NotNull @Size(min = 1) Set<WahlbezirkDTO> wahlbezirke) {
}

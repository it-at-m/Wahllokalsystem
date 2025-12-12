package de.muenchen.oss.wahllokalsystem.authservice.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import lombok.Builder;

@Builder
public record UserDTO(
    @NotNull String username,
    String email,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean userEnabled,
    String wahltagID,
    @JsonFormat(pattern = "yyyy-MM-dd") // to avoid date being converted to array
        LocalDate wahltag,
    String wahlbezirkID,
    String wahlbezirkNummer,
    WahlbezirksartDTO wahlbezirksArt,
    String pin,
    @NotNull Set<String> authorities,
    String wbid_wahlnummer) {}

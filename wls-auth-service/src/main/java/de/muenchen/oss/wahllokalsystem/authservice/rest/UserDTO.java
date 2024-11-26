package de.muenchen.oss.wahllokalsystem.authservice.rest;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import lombok.Builder;

@Builder
public record UserDTO(@NotNull String username,
                      @NotNull String email,
                      @NotNull boolean userEnabled,
                      @NotNull String wahltagID,
                      @NotNull LocalDate wahltag,
                      @NotNull String wahlbezirkID,
                      @NotNull String wahlbezirkNummer,
                      @NotNull WahlbezirksartDTO wahlbezirksArt,
                      @NotNull String pin,
                      @NotNull Set<String> authorities,
                      @NotNull String wbid_wahlnummer) {
}

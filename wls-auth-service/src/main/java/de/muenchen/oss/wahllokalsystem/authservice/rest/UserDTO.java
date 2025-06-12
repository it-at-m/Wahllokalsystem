package de.muenchen.oss.wahllokalsystem.authservice.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import lombok.Builder;

@Builder
public record UserDTO(@NotNull String username,
                      @NotNull String email,
                      @NotNull boolean userEnabled,
                      @NotNull String wahltagID,
                      @JsonFormat(pattern = "yyyy-MM-dd") // to avoid date being converted to array
                      @NotNull LocalDate wahltag,
                      @NotNull String wahlbezirkID,
                      @NotNull String wahlbezirkNummer,
                      @NotNull WahlbezirksartDTO wahlbezirksArt,
                      @NotNull String pin,
                      @NotNull Set<String> authorities,
                      @NotNull String wbid_wahlnummer) {
}

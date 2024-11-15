package de.muenchen.oss.wahllokalsystem.authservice.service;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import lombok.Builder;

@Builder
public record UserModel(@NotNull String username,
                        @NotNull String email,
                        @NotNull boolean userEnabled,
                        @NotNull String wahltagID,
                        @NotNull LocalDate wahltag,
                        @NotNull String wahlbezirkID,
                        @NotNull String wahlbezirkNummer,
                        @NotNull WahlbezirksartModel wahlbezirksArt,
                        @NotNull String pin,
                        @NotNull Set<String> authorities,
                        @NotNull String wbid_wahlnummer
) {

}

package de.muenchen.oss.wahllokalsystem.authservice.service;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import lombok.Builder;

@Builder
public record UserModel(
    @NotNull String username,
    String email,
    boolean userEnabled,
    String wahltagID,
    LocalDate wahltag,
    String wahlbezirkID,
    String wahlbezirkNummer,
    WahlbezirksartModel wahlbezirksArt,
    String pin,
    @NotNull Set<String> authorities,
    String wbid_wahlnummer) {}

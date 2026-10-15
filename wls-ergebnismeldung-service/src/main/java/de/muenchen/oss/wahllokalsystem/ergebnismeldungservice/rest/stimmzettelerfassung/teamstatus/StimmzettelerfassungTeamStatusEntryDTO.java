package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.teamstatus;

import jakarta.validation.constraints.NotNull;

public record StimmzettelerfassungTeamStatusEntryDTO(
    @NotNull String teamID, @NotNull ErfassungTeamStatusDTO status) {}

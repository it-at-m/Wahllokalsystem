package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.teamstatus;

import jakarta.validation.constraints.NotNull;

public record StimmzettelerfassungTeamStatusDTO(@NotNull ErfassungTeamStatusDTO status) {}

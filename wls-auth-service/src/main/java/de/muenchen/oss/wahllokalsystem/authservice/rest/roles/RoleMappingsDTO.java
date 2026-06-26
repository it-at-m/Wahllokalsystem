package de.muenchen.oss.wahllokalsystem.authservice.rest.roles;

import jakarta.validation.constraints.NotNull;

public record RoleMappingsDTO(@NotNull String schriftfuehrerin, @NotNull String admin) {}

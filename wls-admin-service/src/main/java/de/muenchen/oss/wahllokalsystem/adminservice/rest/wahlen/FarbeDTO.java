package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahlen;

import jakarta.validation.constraints.NotNull;

public record FarbeDTO(@NotNull long r, @NotNull long g, @NotNull long b) {
}

package de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen;

import jakarta.validation.constraints.NotNull;

public record FarbeModel(@NotNull long r, @NotNull long g, @NotNull long b) {
}

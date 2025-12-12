package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.referendumvorlagen;

import jakarta.validation.constraints.NotNull;

public record ReferendumoptionModel(@NotNull String id, @NotNull String name, Long position) {}

package de.muenchen.oss.wahllokalsystem.eaiservice.service.init;

import jakarta.validation.constraints.NotNull;

public record InitKandidatModel(
        @NotNull String name,
        long listenposition
) {
}

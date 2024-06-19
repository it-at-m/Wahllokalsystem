package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlvorschlaege;

import jakarta.validation.constraints.NotNull;

public record WLSKandidatDTO(@NotNull String identifikator,
                             @NotNull String name,
                             @NotNull Long listenposition,
                             @NotNull Boolean direktkandidat,
                             @NotNull Long tabellenSpalteInNiederschrift,
                             @NotNull Boolean einzelbewerber) {
}

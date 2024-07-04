package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlvorschlag;

import jakarta.validation.constraints.NotNull;

public record KandidatDTO(@NotNull String identifikator,
                          @NotNull String name,
                          @NotNull Long listenposition,
                          @NotNull Boolean direktkandidat,
                          @NotNull Long tabellenSpalteInNiederschrift,
                          @NotNull Boolean einzelbewerber) {
}

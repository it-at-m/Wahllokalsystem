package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record KandidatModel(@NotNull String identifikator,
                            @NotNull String name,
                            @NotNull long listenposition,
                            @NotNull boolean direktkandidat,
                            @NotNull long tabellenSpalteInNiederschrift,
                            @NotNull boolean einzelbewerber
) {

}

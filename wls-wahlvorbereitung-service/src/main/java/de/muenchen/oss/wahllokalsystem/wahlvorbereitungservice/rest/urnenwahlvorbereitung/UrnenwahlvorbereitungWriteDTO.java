package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.common.WahlurneDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Builder;

@Builder
public record UrnenwahlvorbereitungWriteDTO(@Schema(requiredMode = Schema.RequiredMode.REQUIRED) long anzahlWahlkabinen,
                                            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long anzahlWahltische,
                                            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long anzahlNebenraeume,
                                            @NotNull @Size(min = 1) List<WahlurneDTO> urnenAnzahl) {

    public UrnenwahlvorbereitungWriteDTO(final long anzahlWahlkabinen, final long anzahlWahltische, final long anzahlNebenraeume,
        final List<WahlurneDTO> urnenAnzahl) {
        this.anzahlWahlkabinen = anzahlWahlkabinen;
        this.anzahlWahltische = anzahlWahltische;
        this.anzahlNebenraeume = anzahlNebenraeume;
        this.urnenAnzahl = Objects.requireNonNullElseGet(urnenAnzahl, ArrayList::new);
    }
}

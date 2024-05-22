package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.common.WahlurneDTO;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Builder;

@Builder
public record UrnenwahlvorbereitungWriteDTO(long anzahlWahlkabinen, long anzahlWahltische, long anzahlNebenraeume,
                                            @NotNull List<WahlurneDTO> urnenAnzahl) {

    public UrnenwahlvorbereitungWriteDTO(final long anzahlWahlkabinen, final long anzahlWahltische, final long anzahlNebenraeume,
            final List<WahlurneDTO> urnenAnzahl) {
        this.anzahlWahlkabinen = anzahlWahlkabinen;
        this.anzahlWahltische = anzahlWahltische;
        this.anzahlNebenraeume = anzahlNebenraeume;
        this.urnenAnzahl = Objects.requireNonNullElseGet(urnenAnzahl, ArrayList::new);
    }
}

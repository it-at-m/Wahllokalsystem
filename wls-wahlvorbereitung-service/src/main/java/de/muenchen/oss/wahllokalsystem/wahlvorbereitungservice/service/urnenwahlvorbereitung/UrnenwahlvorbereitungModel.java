package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.common.WahlurneModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Builder;

@Builder
public record UrnenwahlvorbereitungModel(String wahlbezirkID, long anzahlWahlkabinen, long anzahlWahltische, long anzahlNebenraeume,
                                         List<WahlurneModel> urnenAnzahl) {

    public UrnenwahlvorbereitungModel(final String wahlbezirkID, final long anzahlWahlkabinen, final long anzahlWahltische, final long anzahlNebenraeume,
            final List<WahlurneModel> urnenAnzahl) {
        this.wahlbezirkID = wahlbezirkID;
        this.anzahlWahlkabinen = anzahlWahlkabinen;
        this.anzahlWahltische = anzahlWahltische;
        this.anzahlNebenraeume = anzahlNebenraeume;
        this.urnenAnzahl = Objects.requireNonNullElseGet(urnenAnzahl, ArrayList::new);
    }
}

package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.common.WahlurneDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record UrnenwahlvorbereitungDTO(String wahlbezirkID, long anzahlWahlkabinen, long anzahlWahltische, long anzahlNebenraeume,
                                       List<WahlurneDTO> urnenAnzahl) {

    public UrnenwahlvorbereitungDTO(final String wahlbezirkID, final long anzahlWahlkabinen, final long anzahlWahltische, final long anzahlNebenraeume,
            final List<WahlurneDTO> urnenAnzahl) {
        this.wahlbezirkID = wahlbezirkID;
        this.anzahlWahlkabinen = anzahlWahlkabinen;
        this.anzahlWahltische = anzahlWahltische;
        this.anzahlNebenraeume = anzahlNebenraeume;
        this.urnenAnzahl = Objects.requireNonNullElseGet(urnenAnzahl, ArrayList::new);
    }
}

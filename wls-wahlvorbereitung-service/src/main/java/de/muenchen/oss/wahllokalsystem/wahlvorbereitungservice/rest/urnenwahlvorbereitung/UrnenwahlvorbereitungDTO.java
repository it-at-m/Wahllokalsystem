package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.common.WahlurneDTO;
import java.util.List;

public record UrnenwahlvorbereitungDTO(String wahlbezirkID, long anzahlWahlkabinen, long anzahlWahltische, long anzahlNebenraeume,
                                       List<WahlurneDTO> urnenAnzahl) {
}

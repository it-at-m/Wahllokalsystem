package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.common.WahlurneModel;
import java.util.List;
import lombok.Builder;

@Builder
public record UrnenwahlvorbereitungModel(String wahlbezirkID, long anzahlWahlkabinen, long anzahlWahltische, long anzahlNebenraeume,
                                         List<WahlurneModel> urnenAnzahl) {
}

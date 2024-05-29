package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.model;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.model.Wahlart;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record Ergebnismeldung(@NotNull String wahlbezirkID,
                              @NotNull String wahlID,
                              Meldungsart meldungsart,
                              AWerte aWerte,
                              BWerte bWerte,
                              WahlbriefeWerte wahlbriefeWerte,
                              List<UngueltigeStimmzettel> ungueltigeStimmzettels,
                              Long ungueltigeStimmzettelAnzahl,
                              List<Ergebnis> ergebnisse,
                              Wahlart wahlart) {
}

package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.dto.WahlartDTO;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ErgebnismeldungDTO(@NotNull String wahlbezirkID,
                                 @NotNull String wahlID,
                                 MeldungsartDTO meldungsart,
                                 AWerteDTO aWerte,
                                 BWerteDTO bWerte,
                                 WahlbriefeWerteDTO wahlbriefeWerte,
                                 List<UngueltigeStimmzettelDTO> ungueltigeStimmzettels,
                                 Long ungueltigeStimmzettelAnzahl,
                                 List<ErgebnisDTO> ergebnisse,
                                 WahlartDTO wahlart) {
}

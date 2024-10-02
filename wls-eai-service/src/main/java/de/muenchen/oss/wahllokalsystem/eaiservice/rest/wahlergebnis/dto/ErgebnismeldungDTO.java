package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.dto.WahlartDTO;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Builder;

@Builder
public record ErgebnismeldungDTO(@NotNull String wahlbezirkID,
                                 @NotNull String wahlID,
                                 MeldungsartDTO meldungsart,
                                 AWerteDTO aWerte,
                                 BWerteDTO bWerte,
                                 WahlbriefeWerteDTO wahlbriefeWerte,
                                 Set<UngueltigeStimmzettelDTO> ungueltigeStimmzettels,
                                 Long ungueltigeStimmzettelAnzahl,
                                 Set<ErgebnisDTO> ergebnisse,
                                 WahlartDTO wahlart) {
}

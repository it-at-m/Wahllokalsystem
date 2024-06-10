package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record WaehlerverzeichnisModel(@NotNull BezirkIDUndWaehlerverzeichnisNummer waehlerverzeichnisReference,
                                      Boolean verzeichnisLagVor,
                                      Boolean berichtigungVorBeginnDerAbstimmung,
                                      Boolean nachtraeglicheBerichtigung,
                                      Boolean mitteilungUeberUngueltigeWahlscheineErhalten) {
}

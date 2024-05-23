package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import jakarta.validation.constraints.NotNull;

public record WaehlerverzeichnisModel(@NotNull BezirkIDUndWaehlerverzeichnisNummer bezirkIDUndWaehlerverzeichnisNummer,
                                      Boolean verzeichnisLagVor,
                                      Boolean berichtigungVorBeginnDerAbstimmung,
                                      Boolean nachtraeglicheBerichtigung,
                                      Boolean mitteilungUeberUngueltigeWahlscheineErhalten) {
}

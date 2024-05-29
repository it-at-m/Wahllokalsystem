package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.waehlerverzeichnis;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import jakarta.validation.constraints.NotNull;

public record WaehlerverzeichnisDTO(@NotNull BezirkIDUndWaehlerverzeichnisNummer bezirkIDUndWaehlerverzeichnisNummer,
                                    Boolean verzeichnisLagVor,
                                    Boolean berichtigungVorBeginnDerAbstimmung,
                                    Boolean nachtraeglicheBerichtigung,
                                    Boolean mitteilungUeberUngueltigeWahlscheineErhalten) {
}

package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record StimmabgabevermerkeModel(@NotNull BezirkIDUndWaehlerverzeichnisNummer bezirkIDUndWaehlerverzeichnisNummer,
                                       @NotNull long anzahlBlaetter,
                                       @NotNull Set<WahldatenModel> wahldaten) {
}
